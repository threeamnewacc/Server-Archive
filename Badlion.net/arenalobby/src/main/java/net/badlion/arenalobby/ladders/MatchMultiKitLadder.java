package net.badlion.arenalobby.ladders;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.matchmaking.MatchMakingService;

import java.util.List;
import java.util.Random;

public class MatchMultiKitLadder extends Ladder {

	private static final Random random = new Random();

	public MatchMultiKitLadder(int ladderId, List<KitRuleSet> kitRuleSets, MatchMakingService matchMakingService, ArenaCommon.LadderType ladderType,
	                           boolean isRanked, boolean countsTowardsLimit) {
		super(ladderId, kitRuleSets, matchMakingService, ladderType, isRanked, countsTowardsLimit);
	}
}
