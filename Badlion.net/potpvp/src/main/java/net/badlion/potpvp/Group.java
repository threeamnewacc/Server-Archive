package net.badlion.potpvp;

import net.badlion.gberry.Gberry;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Group implements Cloneable {

    private Party party;
    private Player player;
    private List<Player> unmodifiablePlayerList;

    public Group(Party party) {
        Gberry.log("GROUP", party.getPartyLeader().getName() + " added to party group");
        this.party = party;

        // Add to state machine
        GroupStateMachine.getInstance().setCurrentState(this, GroupStateMachine.partyState);
        GroupStateMachine.partyState.add(this, true);
    }

    public Group(Party party, byte b) {
        Gberry.log("GROUP", party.getPartyLeader().getName() + " added to party group");
        this.party = party;
    }

    public Group(Party party, State<Group> state) {
        Gberry.log("GROUP", party.getPartyLeader().getName() + " added to party group");
        this.party = party;

        // Add to state machine
        GroupStateMachine.getInstance().setCurrentState(this, state);
        state.add(this, true);
    }

    public Group(Player player) {
        Gberry.log("GROUP", player.getName() + " added to player group");
        this.player = player;

        // Add to state machine
        GroupStateMachine.getInstance().setCurrentState(this, GroupStateMachine.loginState);
        GroupStateMachine.loginState.add(this, true);
        try {
            GroupStateMachine.loginState.transition(GroupStateMachine.lobbyState, this);
        } catch (IllegalStateTransitionException e) {
            PotPvP.getInstance().somethingBroke(player, this);
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

    public boolean isParty() {
        return this.party != null;
    }

    /**
     * Most of the time when we call this we are modifying the party so mark cache as dirty
     */
    public Party getParty() {
        this.unmodifiablePlayerList = null;

        return this.party;
    }

    public Player getLeader() {
        return this.player != null ? this.player : this.party.getPartyLeader();
    }

    public boolean contains(Player player) {
        return player.equals(this.player) || (this.party != null && this.party.getPlayers().contains(player));
    }

    public List<Player> sortedPlayers() {
        List<Player> players = new ArrayList<>();
        if (this.player != null) {
            players.add(player);
        } else {
            players.addAll(party.getPlayers());
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
        } else {
            players.addAll(party.getPlayers());
        }

        this.unmodifiablePlayerList = Collections.unmodifiableList(players);
        return this.unmodifiablePlayerList;
    }

    public class PlayerSorter implements Comparator<Player> {

        public int compare(Player one, Player another){
            return one.getUniqueId().compareTo(another.getUniqueId());
        }

    }

    public boolean hasDeadPlayers() {
        if (this.player != null) {
	        return this.player.isDead();
        } else {
            for (Player pl : this.party.getPlayers()) {
                if (pl.isDead()) {
                    return true;
                }
            }
        }

        return false;
    }

    public String getDeadPlayerString() {
        if (this.player != null && this.player.isDead()) {
            return this.player.getName();
        }

        StringBuilder builder = new StringBuilder();
        boolean firstPassed = false;
        for (Player pl : this.party.getPlayers()) {
            if (pl.isDead()) {
                if (firstPassed) {
                    builder.append(", ");
                }

                firstPassed = true;
                builder.append(pl.getName());
            }
        }

        return builder.toString();
    }

    @Override
    public Group clone() {
        Group group;

        if (this.isParty()) {
            Party party = new Party(this.party.getPartyLeader());
            for (Player p : this.party.getPlayers()) {
                party.addPlayerPrivately(p);
            }

            group = new Group(party, (byte)0); // Don't add to state machine
        } else {
            group = new Group(this.player, (byte)0); // Don't add to state machine
        }

        return group;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.player != null) {
            builder.append(this.player.getName());
        } else {
            for (int i = 0; i < this.party.getPlayers().size(); i++) {
                if (i != 0) {
                    builder.append(", ");
                }

                builder.append(this.party.getPlayers().get(i).getName());
            }
        }

        return builder.toString();
    }

    /**
     * Send leader a message (usually from trying to do something)
     */
    public void sendLeaderMessage(String msg) {
        if (this.isParty()) {
            this.party.getPartyLeader().sendMessage(msg);
        } else {
            this.player.sendMessage(msg);
        }
    }

	/**
	 * Send a message to everyone but leader
	 */
	public void sendMessageWithoutLeader(String msg) {
		if (this.isParty()) {
			for (Player p : this.party.getPlayers()) {
				if (p != this.party.getPartyLeader()) {
					p.sendMessage(msg);
				}
			}
		} else {
			this.player.sendMessage(msg);
		}
	}

	/**
	 * Send a message to the whole group
	 */
	public void sendMessage(String msg) {
		if (this.isParty()) {
			for (Player p : this.party.getPlayers()) {
				p.sendMessage(msg);
			}
		} else {
			this.player.sendMessage(msg);
		}
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
    };

}
