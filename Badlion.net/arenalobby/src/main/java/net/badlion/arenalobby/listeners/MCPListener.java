package net.badlion.arenalobby.listeners;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.settings.ArenaSettings;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.DuelHelper;
import net.badlion.arenalobby.helpers.RankUpHelper;
import net.badlion.arenalobby.inventories.duel.DuelChooseKitInventory;
import net.badlion.arenalobby.inventories.duel.DuelRequestInventory;
import net.badlion.arenalobby.inventories.lobby.ChatLobbySelectorInventory;
import net.badlion.arenalobby.inventories.lobby.EventQueueInventory;
import net.badlion.arenalobby.inventories.lobby.FFAInventory;
import net.badlion.arenalobby.inventories.lobby.LobbySelectorInventory;
import net.badlion.arenalobby.inventories.lobby.RankedInventory;
import net.badlion.arenalobby.inventories.lobby.Unranked1v1Inventory;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.ArenaSettingsManager;
import net.badlion.arenalobby.managers.DuelRequestManager;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.SidebarManager;
import net.badlion.arenalobby.managers.TournamentManager;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.MCPKeepAliveEvent;
import net.badlion.gberry.events.MCPKeepAliveFailedEvent;
import net.badlion.gberry.utils.Pair;
import net.badlion.gberry.utils.RatingUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class MCPListener implements Listener {

	public static boolean shutdown = false;

	public static boolean resendingArenaBoot = false;

	// For players not online when their rankup comes in, store it for a few seconds
	private Map<Pair<String, String>, Long> rankUpQueue = new HashMap<>();


	@EventHandler
	public void onMCPKeepAliveEvent(MCPKeepAliveEvent event) {
		// Is this a response?
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();

			if (jsonObject == null) return;

			// FFA player counts
			if (jsonObject.containsKey("ffa_player_counts")) {
				FFAInventory.updateFFAInventory((Map<String, String>) jsonObject.get("ffa_player_counts"));
			}
		}
	}


	@EventHandler
	public void onMCPPlayerRankedUp(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();

			// Loop all the rankups that didn't go through since the player was not online, try and do it again if they are now online
			Iterator<Map.Entry<Pair<String, String>, Long>> rankUpQueueIterator = this.rankUpQueue.entrySet().iterator();
			while (rankUpQueueIterator.hasNext()) {
				Map.Entry<Pair<String, String>, Long> entry = rankUpQueueIterator.next();

				Player player = Bukkit.getPlayer(UUID.fromString(entry.getKey().getA()));
				if (player != null) {
					// Get the ranking
					RatingUtil.Rank rank = null;
					for (RatingUtil.Rank rank1 : RatingUtil.Rank.values()) {
						if (rank1.getName().equals(entry.getKey().getB())) {
							rank = rank1;
						}
					}

					if (rank != null) {
						// Play the effect
						RankUpHelper.handleRankedUp(player, rank);
					}
					rankUpQueueIterator.remove();
				} else {
					// If they have not joined in 5 seconds remove
					if ((System.currentTimeMillis() - entry.getValue()) > 5000) {
						rankUpQueueIterator.remove();
					}
				}
			}

			if (jsonObject == null) return;

			if (jsonObject.containsKey("rank_up")) {

				Bukkit.getLogger().log(Level.INFO, "RANKUP: " + jsonObject.get("rank_up").toString());

				List<Map<String, String>> playerRankUps = (List<Map<String, String>>) jsonObject.get("rank_up");
				for (Map<String, String> map : playerRankUps) {
					for (Map.Entry<String, String> entry : map.entrySet()) {
						// Get the player
						Player player = Bukkit.getPlayer(UUID.fromString(entry.getKey()));
						if (player != null) {

							// Get the ranking
							RatingUtil.Rank rank = null;
							for (RatingUtil.Rank rank1 : RatingUtil.Rank.values()) {
								if (rank1.getName().equals(entry.getValue())) {
									rank = rank1;
								}
							}

							if (rank != null) {
								// Play the effect
								RankUpHelper.handleRankedUp(player, rank);
							}
						} else {
							JSONObject queueItem = new JSONObject();
							queueItem.put(entry.getKey(), entry.getValue());
							this.rankUpQueue.put(new Pair<>(entry.getKey(), entry.getValue()), System.currentTimeMillis());
						}
					}
				}
			}

		}
	}

	@EventHandler
	public void onMcpShutdown(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			if (event.getJsonObject() != null) {
				if (event.getJsonObject().containsKey("shutdown_server")) {
					if (Boolean.valueOf((String) event.getJsonObject().get("shutdown_server"))) {
						Bukkit.getLogger().log(Level.INFO, "MCP KEEPALIVE SHUTDOWN");
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
					}
				}
			}
		}

		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.SEND)) {
			JSONObject extraData = (JSONObject) event.getJsonObject().get("extra_data");
			if (shutdown) {
				extraData.put("ready_for_shutdown", "true");
				shutdown = false;
			}
			if (!extraData.isEmpty()) {
				Bukkit.getLogger().log(Level.INFO, "EXTRADATA: " + extraData.toString());
			}
		}
	}


	@EventHandler
	public void onMCPPendingDuelRequest(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}

			List<Map<String, String>> duelRequests = (List<Map<String, String>>) jsonObject.get("pending_duel_requests");
			if (duelRequests != null) {
				ArenaLobby.getInstance().getLogger().log(Level.INFO, "[Got keep alive]: " + duelRequests.toString());
				for (final Map<String, String> request : duelRequests) {
					final Player sender = Bukkit.getPlayer(UUID.fromString(request.get("sender_uuid")));
					if (sender != null) {
						UUID targetId = UUID.fromString(request.get("target_uuid"));
						String targetName = request.get("target_username");
						DuelHelper.DuelCreator duelCreator = new DuelHelper.DuelCreator(sender.getUniqueId(), targetId);
						duelCreator.setReceiverName(targetName);
						DuelChooseKitInventory.openDuelChooseKitInventory(sender);
					}
				}
			}
		}
	}

	@EventHandler
	public void onMCPKeepAliveDuelRequests(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}
			List<Map<String, String>> duelRequests = (List<Map<String, String>>) jsonObject.get("duel_requests");
			if (duelRequests != null) {
				ArenaLobby.getInstance().getLogger().log(Level.INFO, "[Got keep alive]: " + duelRequests.toString());
				for (final Map<String, String> request : duelRequests) {
					final Player target = Bukkit.getPlayer(UUID.fromString(request.get("target_uuid")));
					if (target != null) {
						Group group = ArenaLobby.getInstance().getPlayerGroup(target);
						final UUID senderId = UUID.fromString(request.get("sender_uuid"));
						String ladder = request.get("ladder");
						Integer bestOf = 1;
						if (request.get("best_of") != null) {
							bestOf = Integer.valueOf(request.get("best_of"));
						}

						final DuelHelper.DuelCreator duelCreator;
						String senderName = request.get("sender_name");
						if (DuelRequestManager.getDuelCreator(senderId) == null) {
							duelCreator = new DuelHelper.DuelCreator(senderId, target.getUniqueId());
							duelCreator.setKitRuleSet(KitRuleSet.getKitRuleSet(ladder));
							duelCreator.setSenderName(senderName);
						} else {
							duelCreator = DuelRequestManager.getDuelCreator(senderId);
							duelCreator.setSenderName(senderName);
						}
						duelCreator.setBestOf(bestOf);

						ArenaSettings arenaSettings = ArenaSettingsManager.getSettings(target);
						if (arenaSettings.getDuelRequestType().equals(ArenaSettings.DuelRequestType.CHAT)) {
							// Send chat msg
							BaseComponent[] components = new ComponentBuilder(duelCreator.getKitRuleSet().getName() + " best of " + bestOf + " duel request from ")
									.color(net.md_5.bungee.api.ChatColor.BLUE)
									.color(net.md_5.bungee.api.ChatColor.GREEN).append(senderName + ".")
									.color(net.md_5.bungee.api.ChatColor.GOLD)
									.append(" Click to accept.").color(net.md_5.bungee.api.ChatColor.BLUE)
									.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("Click to accept duel.")).create()))
									.event(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/accept " + senderName)).create();
							target.spigot().sendMessage(components);
						} else if (arenaSettings.getDuelRequestType().equals(ArenaSettings.DuelRequestType.INVENTORY)) {
							//Open inventory
							DuelRequestInventory.openDuelRequestInventory(senderName, senderId, group, duelCreator.isRedRover(), bestOf);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onTournamentUpdate(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}
			if (jsonObject.containsKey("add_tournaments")) {
				List<Map<String, String>> addTournaments = (List<Map<String, String>>) jsonObject.get("add_tournaments");
				for (Map<String, String> tournament : addTournaments) {
					UUID uuid = UUID.fromString(tournament.get("id"));
					String ladderName = tournament.get("ladder");
					String ladderType = tournament.get("type");
					Ladder ladder = LadderManager.getLadder(ladderName, LadderManager.ladderStringToType(ladderType));
					int slots = Integer.valueOf(tournament.get("slots"));
					TournamentManager.addTournament(new TournamentManager.Tournament(uuid, ladder, slots));
				}
			}
			if (jsonObject.containsKey("remove_tournaments")) {
				List<String> removeTournaments = (List<String>) jsonObject.get("remove_tournaments");
				for (String tournamentId : removeTournaments) {
					TournamentManager.Tournament tournament = TournamentManager.getTournament(UUID.fromString(tournamentId));
					if (tournament != null) {
						TournamentManager.removeTournament(tournament);
					}
				}
			}
		}
	}

	@EventHandler
	public void onTotalPlayers(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}
			if (jsonObject.containsKey("total")) {
				double total = (double) jsonObject.get("total");
				SidebarManager.totalOnArena = (int) total;
			}
			if (jsonObject.containsKey("in_game")) {
				double total = (double) jsonObject.get("in_game");
				SidebarManager.totalInGames = (int) total;
			}
		}
	}

	@EventHandler
	public void onUHCMeetupPlayers(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}

			boolean updateEventsInventory = false;
			if (jsonObject.containsKey("in_meetup_queue")) {
				Bukkit.getLogger().log(Level.INFO, "[Meetup in queue]: " + jsonObject.get("in_meetup_queue"));

				Long total = (Long) jsonObject.get("in_meetup_queue");
				LadderManager.uhcMeetupQueue.setInQueue(total.intValue());
				updateEventsInventory = true;
			}

			if (jsonObject.containsKey("in_meetup_matches")) {
				Bukkit.getLogger().log(Level.INFO, "[Meetup in match]: " + jsonObject.get("in_meetup_matches"));

				Long total = (Long) jsonObject.get("in_meetup_matches");
				LadderManager.uhcMeetupQueue.setInGame(total.intValue());
				updateEventsInventory = true;
			}

			if (updateEventsInventory) {
				Bukkit.getLogger().log(Level.INFO, "KEEPALIVE: " + jsonObject.toString());
				EventQueueInventory.updateQueueInventory();
			}
		}
	}

	@EventHandler
	public void onLobbyInfo(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}

			if (jsonObject.containsKey("lobbies")) {
				JSONArray lobbyArray = (JSONArray) jsonObject.get("lobbies");
				LobbySelectorInventory.updateLobbyInventory(lobbyArray);
			}

			if (jsonObject.containsKey("chat_lobbies")) {
				JSONArray chatLobbiesArray = (JSONArray) jsonObject.get("chat_lobbies");
				ChatLobbySelectorInventory.updateChatLobbyInventory(chatLobbiesArray);
			}
		}
	}

	@EventHandler
	public void onMCPUnrankedQueues(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}

			boolean updateUnranked = false;

			List<Map<String, String>> unrankedQueues = (List<Map<String, String>>) jsonObject.get("unranked_ladders");
			if (unrankedQueues != null) {
				updateUnranked = true;

				//Bukkit.getLogger().log(Level.INFO, "[Unranked Queue Info]: " + unrankedQueues.toString());
				for (Map<String, String> entry : unrankedQueues) {
					String ladderName = entry.get("name");
					String ladderType = entry.get("type");
					int inQueue = Integer.valueOf(entry.get("in_queue"));
					int inGame = Integer.valueOf(entry.get("in_game"));
					Gberry.log("KEEPALIVE", "Ladder: " + ladderName + " Type:" + ladderType + " InGame:" + inGame + " InQueue:" + inQueue);
					Ladder ladder = LadderManager.getLadder(ladderName, LadderManager.ladderStringToType(ladderType));
					if (ladder != null) {
						ladder.setInGame(inGame);
						ladder.setInQueue(inQueue);
					}
				}
			}

			// Unlimited unranked ladders
			if (jsonObject.containsKey("unlimited_unranked_ladders")) {
				updateUnranked = true;

				List<String> ladderIDs = (List<String>) jsonObject.get("unlimited_unranked_ladders");

				// Update the lore of these ladder's items in the unranked 1v1 inventory
				Unranked1v1Inventory.unlimitedUnrankedLadders.clear();
				for (String ladderID : ladderIDs) {
					Unranked1v1Inventory.unlimitedUnrankedLadders.add(LadderManager.getLadder(Integer.valueOf(ladderID), ArenaCommon.LadderType.UNRANKED_1V1));
				}
			}

			if (updateUnranked) {
				// Update unranked 1v1 inventory
				Unranked1v1Inventory.updateUnranked1v1Inventory();
			}

			List<Map<String, String>> rankedQueues = (List<Map<String, String>>) jsonObject.get("ranked_ladders");
			if (rankedQueues != null) {
				//Bukkit.getLogger().log(Level.INFO, "[Ranked Queue Info]: " + rankedQueues.toString());
				for (Map<String, String> entry : rankedQueues) {
					String ladderName = entry.get("name");
					String ladderType = entry.get("type");
					int inQueue = Integer.valueOf(entry.get("in_queue"));
					int inGame = Integer.valueOf(entry.get("in_game"));
					Gberry.log("KEEPALIVE", "Ladder: " + ladderName + " Type:" + ladderType + " InGame:" + inGame + " InQueue:" + inQueue);
					Ladder ladder = LadderManager.getLadder(ladderName, LadderManager.ladderStringToType(ladderType));
					if (ladder != null) {
						ladder.setInGame(inGame);
						ladder.setInQueue(inQueue);
						LadderManager.updateTotalPlayersForLadder(LadderManager.ladderStringToType(ladderType));
					}
				}
				RankedInventory.updateRankedInventory();
			}
		}
	}


	@EventHandler
	public void onMCPPlayerGainLosePoints(MCPKeepAliveEvent event) {
		if (event.getType().equals(MCPKeepAliveEvent.KeepAliveType.RESPONSE)) {
			JSONObject jsonObject = event.getJsonObject();
			if (jsonObject == null) {
				return;
			}

			// Expecting "player_points": [{"points": 245, "type": "win/loss", 'uuid': "<uuid>"}, ...]
			if (jsonObject.containsKey("player_points")) {
				List<JSONObject> playerPoints = (List<JSONObject>) jsonObject.get("player_points");
				for (JSONObject pointObject : playerPoints) {
					RankUpHelper.addGainedLostPoints(pointObject);
				}
			}
		}
	}

	@EventHandler
	public void onMCPFailedKeepAliveEvent(MCPKeepAliveFailedEvent event) {
		if (event.isMcpError() && !MCPListener.resendingArenaBoot) {
			final JSONObject data = new JSONObject();
			data.put("server_name", Gberry.serverName);
			data.put("server_region", Gberry.serverRegion.name().toLowerCase());
			MCPListener.resendingArenaBoot = true;
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						JSONObject response = Gberry.contactMCP("arena-lobby-boot", data);
						ArenaLobby.getInstance().getLogger().log(Level.INFO, "[Resending arena-server-boot request] " + data.toString());
						ArenaLobby.getInstance().getLogger().log(Level.INFO, "[arena-server-boot response] " + response.toString());
					} catch (HTTPRequestFailException e) {
						e.printStackTrace();
					} finally {
						MCPListener.resendingArenaBoot = false;
					}
				}
			}.runTaskAsynchronously(ArenaLobby.getInstance());
		}

	}
}