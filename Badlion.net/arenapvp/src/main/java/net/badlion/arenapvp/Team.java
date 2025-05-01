package net.badlion.arenapvp;

import net.badlion.arenapvp.helper.PlayerHelper;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class Team implements Cloneable {

	private final UUID teamId = UUID.randomUUID();

	// Using uuid since the team needs to be created when the players are not online.
	// Key is player id, value is if they are active(alive)
	private Map<UUID, Boolean> members;

	private List<UUID> checkedInPlayers;

	private String name = "";

	// Used for 1v1s if the player combat logs
	private String memberName = "";

	public Team(Collection<UUID> membersIds, String name) {
		this.members = new HashMap<>();

		for (UUID memberId : membersIds) {
			this.members.put(memberId, true);
		}

		this.name = name;
	}


	public Team(UUID playerId) {
		this.members = new HashMap<>();
		this.members.put(playerId, true);
		this.checkedInPlayers = new ArrayList<>();
	}

	public Team(String name) {
		this.members = new HashMap<>();
		this.checkedInPlayers = new ArrayList<>();
		this.name = name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isParty() {
		return this.members.size() > 1;
	}

	public List<UUID> sortedPlayers() {
		List<UUID> players = new ArrayList<>();
		players.addAll(this.members.keySet());

		Collections.sort(players, new PlayerSorter());

		return players;
	}


	public boolean contains(Player player) {
		return this.members.containsKey(player.getUniqueId());
	}

	public boolean contains(UUID playerId) {
		return this.members.containsKey(playerId);
	}

	public void checkIn(Player player) {
		PlayerHelper.healPlayer(player);
		if (this.checkedInPlayers == null) {
			this.checkedInPlayers = new ArrayList<>();
		}

		this.checkedInPlayers.add(player.getUniqueId());

		if (!this.isParty()) {
			this.memberName = player.getDisguisedName();
		}
	}

	public boolean isCheckedIn(Player player) {
		if (this.checkedInPlayers == null) {
			this.checkedInPlayers = new ArrayList<>();
			return false;
		}

		return this.checkedInPlayers.contains(player.getUniqueId());
	}

	public boolean isTeamCheckedIn() {
		if (this.checkedInPlayers == null) {
			this.checkedInPlayers = new ArrayList<>();
			return false;
		}

		for (UUID memberId : this.members.keySet()) {
			if (!this.checkedInPlayers.contains(memberId)) {
				return false;
			}
		}

		return true;
	}

	public List<Player> members() {
		List<Player> players = new ArrayList<>();
		if (this.members != null && !this.members.isEmpty()) {
			for (UUID memberId : this.members.keySet()) {
				Player player = Bukkit.getPlayer(memberId);
				if (player != null) {
					players.add(player);
				}
			}
		}

		return players;
	}

	public Set<UUID> membersIds() {
		return members.keySet();
	}

	public List<String> memberIdsToString() {
		List<String> ids = new ArrayList<>();
		for (UUID id : this.members.keySet()) {
			ids.add(id.toString());
		}

		return ids;
	}

	public Map<String, List<String>> memberIdsToUuidMap() {
		List<String> ids = new ArrayList<>();
		for (UUID id : this.members.keySet()) {
			ids.add(id.toString());
		}

		Map<String, List<String>> ret = new HashMap<>();
		ret.put("uuids", ids);

		return ret;
	}

	public class PlayerSorter implements Comparator<UUID> {

		public int compare(UUID one, UUID another) {
			return one.compareTo(another);
		}

	}

	public boolean hasActivePlayers() {
		for (Map.Entry<UUID, Boolean> entry : this.members.entrySet()) {
			if (entry.getValue()) {
				return true;
			}
		}

		return false;
	}

	public boolean hasActiveOnlinePlayers() {
		for (Map.Entry<UUID, Boolean> entry : this.members.entrySet()) {
			if (entry.getValue()) {
				Player player = Bukkit.getPlayer(entry.getKey());
				if (player != null && player.isOnline()) {
					return true;
				}
			}
		}

		return false;
	}

	public List<Player> getActivePlayers() {
		List<Player> active = new ArrayList<>();
		for (Map.Entry<UUID, Boolean> entry : this.members.entrySet()) {
			if (entry.getValue()) {
				Player member = Bukkit.getPlayer(entry.getKey());
				if (member != null) {
					active.add(member);
				}
			}
		}

		return active;
	}

	public boolean isActive(Player player) {
		if (this.members.containsKey(player.getUniqueId())) {
			return this.members.get(player.getUniqueId());
		}
		return false;
	}

	public String getName() {
		return this.name;
	}

	public boolean hasDeadPlayers() {
		for (Player pl : this.members()) {
			if (pl.isDead()) {
				return true;
			}
		}
		return false;
	}

	public void handlePlayerDeath(UUID playerId) {
		this.members.put(playerId, false);
	}

	public String getDeadPlayerString() {
		StringBuilder builder = new StringBuilder();
		boolean firstPassed = false;
		for (Player pl : this.members()) {
			if (pl.isDead()) {
				if (firstPassed) {
					builder.append(", ");
				}

				firstPassed = true;
				builder.append(pl.getDisguisedName());
			}
		}

		return builder.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (members().size() == 1) {
			builder.append(members().get(0).getDisguisedName());
		} else {
			builder.append("Team: ");
			builder.append("[");
			for (Player member : members()) {
				builder.append(member.getDisguisedName());
				builder.append(", ");
			}
			builder = new StringBuilder(builder.substring(0, builder.length() - 2));
			builder.append("]");
		}

		return builder.toString();
	}

	public String toActiveString() {
		StringBuilder builder = new StringBuilder();
		if (members().size() == 1) {
			builder.append(members().get(0).getDisguisedName());
		} else {
			builder.append(ChatColor.GRAY + "[");
			for (Player member : members()) {
				if (isActive(member)) {
					builder.append(ChatColor.GREEN);
				} else {
					builder.append(ChatColor.RED);
				}
				builder.append(member.getDisguisedName());
				builder.append(ChatColor.GRAY);
				builder.append(", ");
			}

			builder = new StringBuilder(builder.substring(0, builder.length() - 2));
			builder.append(ChatColor.GRAY + "]");
		}

		return builder.toString();
	}

	@Override
	public Team clone() {
		Team team;
		team = new Team(this.members.keySet(), getName()); // Don't add to state machine
		return team;
	}

	/**
	 * Send a message to the whole group
	 */
	public void sendMessage(String msg) {
		if (!this.members.isEmpty()) {
			for (Player p : this.members()) {
				p.sendMessage(msg);
			}
		}
	}

	public UUID getTeamId() {
		return this.teamId;
	}

	public String getMemberName() {
		return this.memberName;
	}

	public UUID getMemberId() {
		for (UUID playerId : membersIds()) {
			return playerId;
		}

		return null;
	}

	public Map<UUID, Boolean> getMembers() {
		return this.members;
	}


	public Object getCreatePacket(ChatColor color, boolean update) {
		try {
			Object packet = TinyProtocolReferences.scoreboardTeamPacket.newInstance();
			TinyProtocolReferences.teamScoreboardPacketRegisteredName.set(packet, ScoreboardUtil.SAFE_TEAM_PREFIX + this.name);
			TinyProtocolReferences.teamScoreboardPacketAction.set(packet, update ? 2 : 0);
			TinyProtocolReferences.teamScoreboardPacketFlag.set(packet, 1);
			TinyProtocolReferences.teamScoreboardPacketPrefix.set(packet, color.toString());
			TinyProtocolReferences.teamScoreboardPacketSuffix.set(packet, "");
			TinyProtocolReferences.teamScoreboardPacketDisplayName.set(packet, "");
			TinyProtocolReferences.teamScoreboardPacketList.set(packet, members().stream().map(Player::getDisguisedName).collect(Collectors.toList()));
			return packet;
		} catch (Exception ex) {
			return null;
		}
	}

	public Object getRemovePacket() {
		try {
			Object packet = TinyProtocolReferences.scoreboardTeamPacket.newInstance();
			TinyProtocolReferences.teamScoreboardPacketAction.set(packet, 1);
			TinyProtocolReferences.teamScoreboardPacketRegisteredName.set(packet, ScoreboardUtil.SAFE_TEAM_PREFIX + this.name);
			return packet;
		} catch (Exception ex) {
			return null;
		}
	}

	public static class GroupRating {

		private Team team;
		private int rating;

		public GroupRating(Team team, int rating) {
			this.team = team;
			this.rating = rating;
		}

		public int getRating() {
			return this.rating;
		}

		public Team getTeam() {
			return this.team;
		}
	}

}
