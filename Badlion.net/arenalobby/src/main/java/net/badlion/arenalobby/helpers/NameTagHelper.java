package net.badlion.arenalobby.helpers;

import net.badlion.arenacommon.settings.ArenaSettings;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.bukkitevents.RatingRetrievedEvent;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.ArenaSettingsManager;
import net.badlion.arenalobby.managers.RatingManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.Pair;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NameTagHelper implements Listener {

	private static Map<String, Team> allScoreboardTeams = new HashMap<>();
	private static Map<UUID, Team> playerToScoreboardTeam = new HashMap<>();

	@EventHandler
	public void onRatingLoadEvent(RatingRetrievedEvent event) {
		final Player player = Bukkit.getPlayer(event.getUuid());

		// Add the player to the team and add their games played nametag objective after all their ratings have loaded

		if (player == null || !player.isOnline()) {
			return;
		}


		// Send our games played score to everyone online
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (player == online) {
				continue;
			}
			try {
				Object scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
				TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, player.getDisguisedName());
				TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "gamesplayed");
				TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, RatingManager.getTotalMatchesPlayed(player.getUniqueId()));
				TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);

				Gberry.protocol.sendPacket(online, scorePacket);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// Delay setting our team due to chat settings getting called delayed or something?
		new BukkitRunnable() {
			@Override
			public void run() {
				if (player != null && player.isOnline()) {
					try {
						ArenaSettings settings = ArenaSettingsManager.getSettings(player);

						String rankPrefix = "";
						String color = ChatColor.WHITE.toString();
						if(settings.isShowRankPrefix()) {
							rankPrefix = ChatSettingsManager.getChatSettings(player).getGroupPrefix();

							// Nuke the color resets
							for (int r = 0; r < 5; r++) {
								rankPrefix = rankPrefix.replace("§r", "");
							}

							//Bukkit.getLogger().log(Level.INFO, player.getDisplayName() + " RANK: " + rankPrefix + "  char 0:" + player.getDisplayName().charAt(0));

							// Extract the players name color use white if none
							if (player.getDisplayName().charAt(0) == ChatColor.COLOR_CHAR) {
								color = player.getDisplayName().substring(0, 2);
							}
						}
						// Create their team with prefix and suffix
						String finalPrefix = rankPrefix.isEmpty() ? color : rankPrefix + " " + color;
						String highestRank = NameTagHelper.getHighestRankString(player);
						String finalSuffix = highestRank.isEmpty() ? "" : " " + highestRank;

						NameTagHelper.addPlayerToTeam(player, finalPrefix, finalSuffix);

						for (Team team : NameTagHelper.allScoreboardTeams.values()) {
							if(team.players.contains(player)){
								continue;
							}
							if(team.playersSentCreatePacket.contains(player.getUniqueId())){
								continue;
							}
							team.addPlayerSentCreatePacket(player);
							Object createTeamPacket = team.getCreateTeamPacket();
							Gberry.log("NAMETAGS", "Sending packet create team to: " + player.getName() + " for " + team.getKey());
							Gberry.protocol.sendPacket(player, createTeamPacket);
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}.runTaskLater(ArenaLobby.getInstance(), 20L);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();

		try {
			// Create our objective with the title of Games
			Object createObjective = TinyProtocolReferences.scoreboardObjectivePacket.newInstance();
			TinyProtocolReferences.objectiveScoreboardPacketName.set(createObjective, "gamesplayed");
			TinyProtocolReferences.objectiveScoreboardPacketTitle.set(createObjective, "Games");
			// 0 for creating the objective
			TinyProtocolReferences.objectiveScoreboardPacketAction.set(createObjective, 0);

			// Tell the client to display this objective
			Object displayObjective = TinyProtocolReferences.scoreboardDisplayObjectivePacket.newInstance();
			TinyProtocolReferences.displayObjectiveScoreboardPacketName.set(displayObjective, "gamesplayed");
			TinyProtocolReferences.displayObjectiveScoreboardPacketPosition.set(displayObjective, 2);

			// Send both packets
			Gberry.protocol.sendPacket(player, createObjective);
			Gberry.protocol.sendPacket(player, displayObjective);

			// Loop players online and send the player logging in all their scores (Has to be after they got the createObjective packet)
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (player == online) {
					continue;
				}

				Object scorePacket = TinyProtocolReferences.scoreboardScorePacket.newInstance();
				TinyProtocolReferences.scoreScoreboardPacketUsername.set(scorePacket, online.getDisguisedName());
				TinyProtocolReferences.scoreScoreboardPacketObjectiveName.set(scorePacket, "gamesplayed");
				TinyProtocolReferences.scoreScoreboardPacketScore.set(scorePacket, RatingManager.getTotalMatchesPlayed(online.getUniqueId()));
				TinyProtocolReferences.scoreScoreboardPacketAction.set(scorePacket, 0);

				Gberry.protocol.sendPacket(player, scorePacket);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		try {
			// Remove the objective from the client (Important because if you send the same objective two times it could crash the client!)
			// Also since some servers like SA and Asia have the bungee hack where you don't switch worlds, this may not be cleared and cause the client to crash?
			Object removeObjective = TinyProtocolReferences.scoreboardObjectivePacket.newInstance();
			TinyProtocolReferences.objectiveScoreboardPacketName.set(removeObjective, "gamesplayed");
			TinyProtocolReferences.objectiveScoreboardPacketTitle.set(removeObjective, "");
			TinyProtocolReferences.objectiveScoreboardPacketAction.set(removeObjective, 1);

			Gberry.protocol.sendPacket(player, removeObjective);

			// Send them all the remove team packets
			for (Team team : NameTagHelper.allScoreboardTeams.values()) {
				if(team.players.contains(player)){
					continue;
				}

				// Make sure they have the team created
				if(!team.playersSentCreatePacket.contains(player.getUniqueId())){
					continue;
				}
				team.removePlayerSentCreatePacket(player);
				Object removeTeamPacket = team.getRemoveTeamPacket();
				Gberry.log("NAMETAGS", "Sending packet remove team to: " + player.getName() + " for " + team.getKey());
				Gberry.protocol.sendPacket(player, removeTeamPacket);
			}
			NameTagHelper.removeTeam(player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	// Get a nice string for the players rank
	private static String getHighestRankString(Player player) {
		Pair<Ladder, RatingUtil.Rank> highestRank = RatingManager.getHighestRank(player);
		String rankPrefix = "";
		if (highestRank != null) {
			if (!highestRank.getB().equals(RatingUtil.Rank.NONE)) {
				if (highestRank.getB().equals(RatingUtil.Rank.MASTERS)) {
					rankPrefix = highestRank.getB().getChatColor() + "[" + highestRank.getB().getName() + "]";
				} else {
					rankPrefix = highestRank.getB().getChatColor() + "[" + highestRank.getA().getKitRuleSet().getName() + " " + NameTagHelper.numberFromNumeral(highestRank.getB().getName().split(" ")[1]) + "]";
				}
			}
		}
		return rankPrefix;
	}

	private static Integer numberFromNumeral(String string) {
		switch (string) {
			case "V":
				return 5;
			case "IV":
				return 4;
			case "III":
				return 3;
			case "II":
				return 2;
			case "I":
				return 1;
			default:
				return 0;
		}
	}

	public static Team getTeam(String key) {
		if (NameTagHelper.allScoreboardTeams.get(key) != null) {
			return NameTagHelper.allScoreboardTeams.get(key);
		}
		return null;
	}


	// Team junk since bukkit api doesn't work

	private static class Team {

		private String key;
		private String prefix;
		private String suffix;

		private Set<Player> players = new HashSet<>();

		private Set<UUID> playersSentCreatePacket = new HashSet<>();

		public Team(String key, String prefix, String suffix) {
			this.key = key;
			this.prefix = prefix;
			this.suffix = suffix;
		}

		public String getKey() {
			return this.key;
		}

		public void addPlayer(Player player) {
			this.players.add(player);
		}

		public void removePlayer(Player player) {
			this.players.remove(player);
		}

		public void addPlayerSentCreatePacket(Player player){
			this.playersSentCreatePacket.add(player.getUniqueId());
		}

		public void removePlayerSentCreatePacket(Player player){
			this.playersSentCreatePacket.remove(player.getUniqueId());
		}

		public Object getCreateTeamPacket() {
			try {
				Object packet = TinyProtocolReferences.scoreboardTeamPacket.newInstance();
				TinyProtocolReferences.teamScoreboardPacketRegisteredName.set(packet, this.key);
				TinyProtocolReferences.teamScoreboardPacketAction.set(packet, 0);
				TinyProtocolReferences.teamScoreboardPacketFlag.set(packet, 1);
				TinyProtocolReferences.teamScoreboardPacketPrefix.set(packet, this.prefix);
				TinyProtocolReferences.teamScoreboardPacketSuffix.set(packet, this.suffix);
				TinyProtocolReferences.teamScoreboardPacketDisplayName.set(packet, "");
				List<String> playerNames = new ArrayList<>();
				for (Player player : this.players) {
					playerNames.add(player.getDisguisedName());
				}
				Gberry.log("NAMETAGS", "CREATE TEAM: " + key + " " + playerNames.toString());
				TinyProtocolReferences.teamScoreboardPacketList.set(packet, playerNames);
				return packet;
			} catch (Exception ex) {
				return null;
			}
		}

		public Object getRemoveTeamPacket() {
			try {
				Object packet = TinyProtocolReferences.scoreboardTeamPacket.newInstance();
				TinyProtocolReferences.teamScoreboardPacketAction.set(packet, 1);
				TinyProtocolReferences.teamScoreboardPacketRegisteredName.set(packet, this.key);
				return packet;
			} catch (Exception ex) {
				return null;
			}
		}

		public Object getAddPlayerPacket(Player player) {
			try {
				Object packet = TinyProtocolReferences.scoreboardTeamPacket.newInstance();
				TinyProtocolReferences.teamScoreboardPacketRegisteredName.set(packet, this.key);
				TinyProtocolReferences.teamScoreboardPacketAction.set(packet, 3);
				TinyProtocolReferences.teamScoreboardPacketList.set(packet, Arrays.asList(player.getDisguisedName()));
				return packet;
			} catch (Exception ex) {
				return null;
			}
		}

		public Object getRemovePlayerPacket(Player player) {
			try {
				Object packet = TinyProtocolReferences.scoreboardTeamPacket.newInstance();
				TinyProtocolReferences.teamScoreboardPacketRegisteredName.set(packet, this.key);
				TinyProtocolReferences.teamScoreboardPacketAction.set(packet, 4);
				TinyProtocolReferences.teamScoreboardPacketList.set(packet, Arrays.asList(player.getDisguisedName()));
				return packet;
			} catch (Exception ex) {
				return null;
			}
		}

		public int getSize() {
			return players.size();
		}

	}


	// Creates our team with prefix and suffix
	private static void addPlayerToTeam(Player player, String prefix, String suffix) {
		if (prefix == null) {
			prefix = "";
		} else {
			prefix = ChatColor.translateAlternateColorCodes('&', prefix);
			if (prefix.length() > 16) {
				// try find last color code in their prefix
				for (int i = prefix.length() - 2; i >= 0; i--) {
					if (prefix.charAt(i) == ChatColor.COLOR_CHAR) {
						char color = prefix.charAt(i + 1);
						if (color >= '0' && color <= '9' || color >= 'a' && color <= 'f') {
							prefix = new String(new char[]{ChatColor.COLOR_CHAR, color});
							break;
						}
					}
				}
				if (prefix.length() > 16) {
					// failed to find, just remove it
					prefix = "";
				}
			}
		}
		if (suffix == null) {
			suffix = "";
		} else {
			suffix = ChatColor.translateAlternateColorCodes('&', suffix);
			if (suffix.length() > 16) {
				suffix = "";
			}
		}
		String key = Integer.toHexString(("prefix = " + prefix).hashCode()) + Integer.toHexString(("suffix = " + suffix).hashCode());
		String teamName = ScoreboardUtil.SAFE_TEAM_PREFIX + key.substring(0, 14);
		Team team = NameTagHelper.getTeam(teamName);
		if (team == null) {
			// Make a new team
			team = new Team(teamName, prefix, suffix);
			team.addPlayer(player);
			NameTagHelper.allScoreboardTeams.put(teamName, team);
			NameTagHelper.playerToScoreboardTeam.put(player.getUniqueId(), team);

			Object createTeamPacket = team.getCreateTeamPacket();
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online == player) {
					continue;
				}
				if(team.playersSentCreatePacket.contains(online.getUniqueId())){
					continue;
				}
				team.addPlayerSentCreatePacket(online);
				Gberry.log("NAMETAGS", "Sending packet create team to: " + online.getName() + " for " + team.getKey());
				Gberry.protocol.sendPacket(online, createTeamPacket);
			}
		} else {
			// Team already existed

			NameTagHelper.playerToScoreboardTeam.put(player.getUniqueId(), team);
			team.addPlayer(player);
			Object addPlayerPacket = team.getAddPlayerPacket(player);
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (online == player) {
					continue;
				}
				if(team.players.contains(online)){
					continue;
				}

				// Make sure they have the team created
				if(!team.playersSentCreatePacket.contains(online.getUniqueId())){
					continue;
				}
				Gberry.log("NAMETAGS", "Sending packet add player to: " + online.getName() + " for " + team.getKey());
				Gberry.protocol.sendPacket(online, addPlayerPacket);
			}
		}
	}

	public static void removeTeam(Player player) {
		Team team = NameTagHelper.playerToScoreboardTeam.get(player.getUniqueId());
		if (team != null) {
			team.removePlayer(player);
			NameTagHelper.playerToScoreboardTeam.remove(player.getUniqueId());
			if (team.getSize() == 0) {
				NameTagHelper.allScoreboardTeams.remove(team.getKey());
				// Send all players online remove team packet except for the player in the team (since this team was never made for them)
				Object removeTeamPacket = team.getRemoveTeamPacket();
				for (Player online : Bukkit.getOnlinePlayers()) {
					if (online == player) {
						continue;
					}

					// Make sure they have the team created
					if(!team.playersSentCreatePacket.contains(online.getUniqueId())){
						continue;
					}
					team.removePlayerSentCreatePacket(player);
					Gberry.log("NAMETAGS", "Sending packet remove team to: " + online.getName() + " for " + team.getKey());
					Gberry.protocol.sendPacket(online, removeTeamPacket);
				}
			} else {
				Object removePlayerPacket = team.getRemovePlayerPacket(player);
				for (Player online : Bukkit.getOnlinePlayers()) {
					if (online == player) {
						continue;
					}
					if(team.players.contains(online)){
						continue;
					}

					// Make sure they have the team created
					if(!team.playersSentCreatePacket.contains(online.getUniqueId())){
						continue;
					}
					Gberry.log("NAMETAGS", "Sending packet remove player to: " + online.getName() + " for " + team.getKey());
					Gberry.protocol.sendPacket(online, removePlayerPacket);
				}
			}
		}
	}
}
