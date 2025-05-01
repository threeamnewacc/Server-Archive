package net.badlion.arenalobby.matchmaking;

import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.MatchMakingManager;

public abstract class MatchMakingService {

	/**
	 * Add group to matchmaking service
	 */
	public void addGroup(Group group) {
		MatchMakingManager.addToMatchMaking(group, this);
		return;
	}

	/**
	 * Remove group from matchmaking service
	 */
	public abstract boolean removeGroup(Group group, boolean transitionState);

	public abstract Ladder getLadder();

	public abstract void setLadder(Ladder ladder);

	public abstract String getServiceInfo();

	public abstract int getNumberInQueue();

}
