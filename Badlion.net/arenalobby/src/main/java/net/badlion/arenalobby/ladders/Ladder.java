package net.badlion.arenalobby.ladders;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.matchmaking.MatchMakingService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Ladder {

	protected int ladderId;
	protected List<KitRuleSet> kitRuleSets = new ArrayList<>();
	protected MatchMakingService matchMakingService;
	protected ArenaCommon.LadderType ladderType;
	protected boolean isRanked;
	protected boolean countsTowardsLimit;
	protected int inGame = 0;
	protected int inQueue = 0;

	public static int globalRankedLadders = 0;

	public Ladder(int ladderId, KitRuleSet kitRuleSet, MatchMakingService matchMakingService, ArenaCommon.LadderType ladderType, boolean isRanked,
	              boolean countsTowardsLimit) {
		// Check both conditions for special events
		if (ladderType == ArenaCommon.LadderType.RANKED_1V1 && isRanked && countsTowardsLimit) {
			++globalRankedLadders;
		}

		this.ladderId = ladderId;
		this.kitRuleSets.add(kitRuleSet);
		this.matchMakingService = matchMakingService;
		this.matchMakingService.setLadder(this);
		this.ladderType = ladderType;
		this.isRanked = isRanked;
		this.countsTowardsLimit = countsTowardsLimit;
	}

	public Ladder(int ladderId, List<KitRuleSet> kitRuleSets, MatchMakingService matchMakingService, ArenaCommon.LadderType ladderType, boolean isRanked,
	              boolean countsTowardsLimit) {
		this.ladderId = ladderId;
		this.kitRuleSets = kitRuleSets;
		this.matchMakingService = matchMakingService;
		this.matchMakingService.setLadder(this);
		this.ladderType = ladderType;
		this.isRanked = isRanked;
		this.countsTowardsLimit = countsTowardsLimit;
	}

	public boolean hasLimit() {
		return this.countsTowardsLimit;
	}

	public void addGroup(Group group) {
		this.matchMakingService.addGroup(group);
	}

	public KitRuleSet getKitRuleSet() {
		return kitRuleSets.get(0);
	}

	public List<KitRuleSet> getAllKitRuleSets() {
		return Collections.unmodifiableList(this.kitRuleSets);
	}

	public MatchMakingService getMatchMakingService() {
		return matchMakingService;
	}

	public ArenaCommon.LadderType getLadderType() {
		return ladderType;
	}

	public int getLadderId() {
		return ladderId;
	}

	public boolean isRanked() {
		return isRanked;
	}

	public int getInGame() {
		return inGame;
	}

	public int getInQueue() {
		return inQueue;
	}

	public void setInGame(int inGame) {
		this.inGame = inGame;
	}

	public void setInQueue(int inQueue) {
		this.inQueue = inQueue;
	}


	@Override
	public String toString() {
		return this.kitRuleSets.get(0).getName() + " " + this.getLadderType().name();
	}
}
