package net.badlion.arenalobby.ladders;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.matchmaking.MatchMakingService;

public class MatchLadder extends Ladder {

	public MatchLadder(int ladderId, KitRuleSet kitRuleSet, MatchMakingService matchMakingService, ArenaCommon.LadderType ladderType,
	                   boolean isRanked, boolean countsTowardsLimit) {
		super(ladderId, kitRuleSet, matchMakingService, ladderType, isRanked, countsTowardsLimit);
	}
}
