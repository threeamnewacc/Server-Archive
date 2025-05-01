package net.badlion.mpg.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.MCPKeepAliveEvent;
import net.badlion.gberry.events.MCPKeepAliveFailedEvent;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.json.simple.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MatchmakingMCPListener implements Listener {

	public static MPGConfigurator mpgConfigurator;
	public static MPGPlayerCreator mpgPlayerCreator;
	public static MPGLobbyServerSender mpgLobbyServerSender;

	private boolean sentWantAGameRequest = false;
	private UUID keepAliveUUIDWithWantAGameRequest;

	public MatchmakingMCPListener() {
		// Make sure the MPG player creator was set
		if (MatchmakingMCPListener.mpgPlayerCreator == null) {
			throw new RuntimeException("MPG player creator for MPG keep alive task not set!");
		}
	}

	@EventHandler
	public void onMCPKeepAliveEvent(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.SEND)) {
			JSONObject payload = event.getJsonObject();

			JSONObject extraData = (JSONObject) payload.get("extra_data");

			// Always put the server_type in the keep alive
			extraData.put("server_type", Gberry.serverType.getInternalName());

			// Do we need to send the want_a_game request?
			if (!this.sentWantAGameRequest) {
				extraData.put("status", "want_a_game");

				// Send the map name in payload if map voting is enabled
				if (MPG.USES_MAP_VOTING) {
					extraData.put("map_name", MPG.getInstance().getMPGGame().getWorld().getGWorld().getInternalName());
				}

				System.out.println(payload);

				this.sentWantAGameRequest = true;
				this.keepAliveUUIDWithWantAGameRequest = event.getKeepAliveId();
			}
		} else { // RESPONSE
			final JSONObject response = event.getJsonObject();

			// Are we getting a game?
			if (response != null && response.containsKey("match_id")) {
				System.out.println(response);

				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						final Map<UUID, String> uuidUsernames = new HashMap<>();
						final List<List<String>> teamsList = (List<List<String>>) response.get("players");

						for (List<String> uuidStrings : teamsList) {
							// Get all usernames from the UUIDs
							for (String uuidString : uuidStrings) {
								UUID uuid = UUID.fromString(uuidString);
								String username = Gberry.getUsernameFromUUID(uuid);

								// Make sure username is valid
								if (username == null) {
									throw new RuntimeException("Username null for UUID " + uuid + " in MPG keep alive");
								}

								// Is this person disguised?
								JSONObject disguiseSettings = UserDataManager.getUserDataFromDB(uuid).getDisguiseSettings();

								if ((boolean) disguiseSettings.get("is_disguised")) {
									username = (String) disguiseSettings.get("disguise_name");
								}

								uuidUsernames.put(uuid, username);
							}
						}

						// Note: This runs before players join the server, so the
						// MPGCreatePlayerEvent listener does nothing in MPGPlayerManager
						BukkitUtil.runTask(new Runnable() {
							@Override
							public void run() {
								// Figure out which game type this server is going
								// to support and configure the game (call async cuz yolo)
								String gameType = (String) response.get("type");
								if (gameType.equals("ffa")) {
									MatchmakingMCPListener.mpgConfigurator.configureGame(MPG.GameType.FFA, null);
								} else {
									MatchmakingMCPListener.mpgConfigurator.configureGame(MPG.GameType.PARTY, Gberry.getJSONInteger(response, "players_per_team"));
								}

								// Store match id
								MiniStats.MATCH_ID = (String) response.get("match_id");

								// Create MPGPlayer objects for each player
								for (UUID uuid : uuidUsernames.keySet()) {
									String username = uuidUsernames.get(uuid);

									MatchmakingMCPListener.mpgPlayerCreator.createMPGPlayer(uuid, username);
								}

								// Create teams if this is a teams game
								if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
									int counter = 0;
									for (List<String> teamUUIDStrings : teamsList) {
										// Create team with an assigned color
										MPGTeam team = new MPGTeam(MPGTeam.TEAM_COLORS[counter++]);

										// Set clan information for this team if this is a clan game
										if (response.containsKey("sender_clan_id")) {
											// First team is always the sender
											if (counter == 1) {
												team.setClanId(Gberry.getJSONInteger(response, "sender_clan_id"));
												team.setClanName((String) response.get("sender_clan_name"));
											} else {
												team.setClanId(Gberry.getJSONInteger(response, "target_clan_id"));
												team.setClanName((String) response.get("target_clan_name"));
											}
										}

										for (String uuidString : teamUUIDStrings) {
											MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(UUID.fromString(uuidString));

											team.add(mpgPlayer);

											mpgPlayer.setTeam(team);
										}
									}
								}

								MPGGame mpgGame = MPG.getInstance().getMPGGame();

								// Set clan data if this is a clan game
								if (response.containsKey("sender_clan_id")) {
									mpgGame.setIsClanGame(true);
									mpgGame.setSenderClanId(Gberry.getJSONInteger(response, "sender_clan_id"));
									mpgGame.setSenderClanName((String) response.get("sender_clan_name"));
									mpgGame.setTargetClanId(Gberry.getJSONInteger(response, "target_clan_id"));
									mpgGame.setTargetClanName((String) response.get("target_clan_name"));
								}

								MPG.getInstance().setServerState(MPG.ServerState.GAME);

								mpgGame.setGameState(MPGGame.GameState.PRE_GAME);

								// Do this 2 seconds later to allow time for the game to load completely
								BukkitUtil.runTaskLater(new Runnable() {
									@Override
									public void run() {
										// Transition all players to DC state
										for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
											mpgPlayer.setState(MPGPlayer.PlayerState.DC);
										}
									}
								}, 40L);

								// Do this 2 seconds later to allow time for the game to load completely
								BukkitUtil.runTaskLaterAsync(new Runnable() {
									@Override
									public void run() {
										Gberry.sendToServer((Collection) uuidUsernames.keySet(), Gberry.serverName);
									}
								}, 40L);
							}
						});
					}
				});
			}
		}
	}

	@EventHandler
	public void onMCPKeepAliveFailedEvent(MCPKeepAliveFailedEvent event) {
		if (this.keepAliveUUIDWithWantAGameRequest == event.getKeepAliveId()) {
			this.sentWantAGameRequest = false;
			this.keepAliveUUIDWithWantAGameRequest = null;
		}
	}

	public interface MPGPlayerCreator {

		public MPGPlayer createMPGPlayer(UUID uuid, String username);

	}

	public interface MPGConfigurator {

		public void configureGame(MPG.GameType gameType, Integer playersPerTeam);

	}

	public interface MPGLobbyServerSender {

		public void sendPlayersToLobby(List<Player> player);

	}

}
