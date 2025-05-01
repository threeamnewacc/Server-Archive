package net.badlion.arenalobby;

import net.badlion.gberry.Gberry;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Group implements Cloneable {

	private Player player;
	private List<Player> unmodifiablePlayerList;

	public Group(Player player) {
		Gberry.log("GROUP", player.getName() + " added to player group");
		this.player = player;

		// Add to state machine
		GroupStateMachine.getInstance().setCurrentState(this, GroupStateMachine.loginState);
		GroupStateMachine.loginState.add(this, true);
		try {
			GroupStateMachine.loginState.transition(GroupStateMachine.lobbyState, this);
		} catch (IllegalStateTransitionException e) {
			ArenaLobby.getInstance().somethingBroke(player, this);
		}
	}

	public Group(Player player, byte b) {
		Gberry.log("GROUP", player.getName() + " added to player group");
		this.player = player;
	}

	public Group(Player player, boolean fuckingSmellyIsAnnoying) {
		Gberry.log("GROUP", player.getName() + " added to player group");
		this.player = player;

		// Add to state machine
		GroupStateMachine.getInstance().setCurrentState(this, GroupStateMachine.lobbyState);
		GroupStateMachine.lobbyState.add(this, true);
	}


	public Player getLeader() {
		return this.player;
	}

	public boolean contains(Player player) {
		return player.equals(this.player);
	}

	public List<Player> sortedPlayers() {
		List<Player> players = new ArrayList<>();
		if (this.player != null) {
			players.add(player);
		}

		Collections.sort(players, new PlayerSorter());

		return players;
	}

	/**
	 * Allow user to get players in a group but not modify this Group itself
	 */
	public List<Player> players() {
		// Remove later if i feel like dealing with the fact that Party needs to update this with addPlayer and removePlayer
	    /*if (this.unmodifiablePlayerList != null) {
            return this.unmodifiablePlayerList;
        }*/

		List<Player> players = new ArrayList<>();
		if (this.player != null) {
			players.add(player);
		}

		this.unmodifiablePlayerList = Collections.unmodifiableList(players);
		return this.unmodifiablePlayerList;
	}

	public class PlayerSorter implements Comparator<Player> {

		public int compare(Player one, Player another) {
			return one.getUniqueId().compareTo(another.getUniqueId());
		}

	}

	@Override
	public Group clone() {
		return new Group(this.player, (byte) 0); // Don't add to state machine
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.player != null) {
			builder.append(this.player.getName());
		}
		return builder.toString();
	}

	/**
	 * Send leader a message (usually from trying to do something)
	 */
	public void sendLeaderMessage(String msg) {
		this.player.sendMessage(msg);
	}

	/**
	 * Send a message to everyone but leader
	 */
	public void sendMessageWithoutLeader(String msg) {
		this.player.sendMessage(msg);
	}

	/**
	 * Send a message to the whole group
	 */
	public void sendMessage(String msg) {
		this.player.sendMessage(msg);
	}

	public static class GroupRating {

		private Group group;
		private int rating;

		public GroupRating(Group group, int rating) {
			this.group = group;
			this.rating = rating;
		}

		public int getRating() {
			return rating;
		}

		public Group getGroup() {
			return this.group;
		}
	}

	;

}
