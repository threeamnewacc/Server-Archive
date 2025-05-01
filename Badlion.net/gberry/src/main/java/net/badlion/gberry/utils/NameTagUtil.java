package net.badlion.gberry.utils;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class NameTagUtil implements Listener {

	private static Map<String, Team> allScoreboardTeams = new HashMap<>();
	private static Map<UUID, Team> playerToScoreboardTeam = new HashMap<>();

	public static void createPlayerNameTag(Player player, String prefix, String suffix) {
		try {
			// Create their team with prefix and suffix
			NameTagUtil.addPlayerToTeam(player, prefix, suffix);

			NameTagUtil.sendPlayerAllNameTags(player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void sendPlayerAllNameTags(Player player) {
		for (Team team : NameTagUtil.allScoreboardTeams.values()) {
			//if (team.players.contains(player)) continue;

			if (team.playersSentCreatePacket.contains(player.getUniqueId())) continue;

			team.addPlayerSentCreatePacket(player);
			Object createTeamPacket = team.getCreateTeamPacket();
			Gberry.log("NAMETAGS", "Sending packet create team to: " + player.getName() +  " for " + player.getName() + " with key " + team.getKey());
			Gberry.protocol.sendPacket(player, createTeamPacket);
		}
	}

	public static void removePlayerNameTag(Player player) {
		try {
			NameTagUtil.removeAllPlayerNameTags(player);

			NameTagUtil.removeTeam(player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void removeAllPlayerNameTags(Player player) {
		// Send them all the remove team packets
		for (Team team : NameTagUtil.allScoreboardTeams.values()) {
			//if (team.players.contains(player)) continue;

			// Make sure they have the team created
			if (!team.playersSentCreatePacket.contains(player.getUniqueId())) continue;

			team.removePlayerSentCreatePacket(player);

			Object removeTeamPacket = team.getRemoveTeamPacket();

			Gberry.log("NAMETAGS", "Sending packet remove team to: " + player.getName() + " for " + team.getKey());

			Gberry.protocol.sendPacket(player, removeTeamPacket);
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		NameTagUtil.removeAllPlayerNameTags(event.getPlayer());
	}

	public static Team getTeam(String key) {
		return NameTagUtil.allScoreboardTeams.get(key);
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

		Team team = NameTagUtil.getTeam(teamName);
		if (team == null) {
			// Make a new team
			team = new Team(teamName, prefix, suffix);
			team.addPlayer(player);

			NameTagUtil.allScoreboardTeams.put(teamName, team);
			NameTagUtil.playerToScoreboardTeam.put(player.getUniqueId(), team);

			Object createTeamPacket = team.getCreateTeamPacket();
			for (Player online : Bukkit.getOnlinePlayers()) {
				//if (online == player) continue;

				if (team.playersSentCreatePacket.contains(online.getUniqueId())) continue;

				team.addPlayerSentCreatePacket(online);

				Gberry.log("NAMETAGS", "Sending packet create team to: " + online.getName() + " for " + player.getName() + " with key " + team.getKey());
				Gberry.protocol.sendPacket(online, createTeamPacket);
			}
		} else {
			// Team already existed

			NameTagUtil.playerToScoreboardTeam.put(player.getUniqueId(), team);
			team.addPlayer(player);

			Object addPlayerPacket = team.getAddPlayerPacket(player);
			for (Player online : Bukkit.getOnlinePlayers()) {
				//if (online == player) continue;

				//if (team.players.contains(online)) continue;

				// Make sure they have the team created
				if (!team.playersSentCreatePacket.contains(online.getUniqueId())) continue;

				Gberry.log("NAMETAGS", "Sending packet add player to: " + online.getName() +  " for " + player.getName() + " with key " + team.getKey());
				Gberry.protocol.sendPacket(online, addPlayerPacket);
			}
		}
	}

	public static void removeTeam(Player player) {
		Team team = NameTagUtil.playerToScoreboardTeam.get(player.getUniqueId());
		if (team != null) {
			team.removePlayer(player);
			NameTagUtil.playerToScoreboardTeam.remove(player.getUniqueId());

			if (team.getSize() == 0) {
				NameTagUtil.allScoreboardTeams.remove(team.getKey());

				// Send all players online remove team packet except for the player in the team (since this team was never made for them)
				Object removeTeamPacket = team.getRemoveTeamPacket();
				for (Player online : Bukkit.getOnlinePlayers()) {
					//if (online == player) continue;

					// Make sure they have the team created
					if (!team.playersSentCreatePacket.contains(online.getUniqueId())) continue;

					team.removePlayerSentCreatePacket(player);

					Gberry.log("NAMETAGS", "Sending packet remove team to: " + online.getName() +  " for " + player.getName() + " with key " + team.getKey());
					Gberry.protocol.sendPacket(online, removeTeamPacket);
				}
			} else {
				Object removePlayerPacket = team.getRemovePlayerPacket(player);
				for (Player online : Bukkit.getOnlinePlayers()) {
					//if (online == player) continue;

					//if (team.players.contains(online)) continue;

					// Make sure they have the team created
					if (!team.playersSentCreatePacket.contains(online.getUniqueId())) continue;

					Gberry.log("NAMETAGS", "Sending packet remove player to: " + online.getName() +  " for " + player.getName() + " with key " + team.getKey());
					Gberry.protocol.sendPacket(online, removePlayerPacket);
				}
			}
		}
	}

	// Custom Team object since Bukkit API is trash

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
			return this.players.size();
		}

	}

}
