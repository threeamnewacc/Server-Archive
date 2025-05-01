package net.badlion.potpvp.matchmaking;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.MatchMakingManager;

import java.util.List;

public abstract class MatchMakingService {

    /**
     * Add group to matchmaking service
     */
    public void addGroup(Group group, int rating) {
        MatchMakingManager.addToMatchMaking(group, this);
    }

    /**
     * Remove group from matchmaking service
     */
    public abstract boolean removeGroup(Group group);

    /**
     * Request groups to start a new match (might return empty list)
     */
    public abstract List<Group.GroupRating> requestGroups();

    public abstract Ladder getLadder();

    public abstract void setLadder(Ladder ladder);

	public abstract String getServiceInfo();

	public abstract int getNumberInQueue();

}
