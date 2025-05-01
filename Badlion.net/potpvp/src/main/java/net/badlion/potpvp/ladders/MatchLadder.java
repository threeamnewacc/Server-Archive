package net.badlion.potpvp.ladders;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.matchmaking.MatchMakingService;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.List;

public class MatchLadder extends Ladder {

    public MatchLadder(int ladderId, KitRuleSet kitRuleSet, MatchMakingService matchMakingService, LadderType ladderType,
                       boolean isRanked, boolean countsTowardsLimit) {
        super(ladderId, kitRuleSet, matchMakingService, ladderType, isRanked, countsTowardsLimit);
    }

    @Override
    public Game createGame() {
        return new Match(null, this.isRanked, this.getKitRuleSet()); // Temp set arena to null
    }

    @Override
    public boolean addPlayersToGame(Game game) {
        if (!(game instanceof Match)) {
            throw new RuntimeException("Invalid game passed to MatchLadder " + this.ladderId);
        }

        Match match = (Match) game;
        List<Group.GroupRating> groups = this.matchMakingService.requestGroups();

        if (groups.size() < 2) {
            // Something went wrong
            for (Group.GroupRating group : groups) {
                this.matchMakingService.addGroup(group.getGroup(), group.getRating());
            }

            return false;
        }

        // Validate we have the right number of players to start this
        if (!this.validateCorrectNumberOfPlayers(groups.get(0), groups.get(1))) {
            return false;
        }

	    // Validate that elo gained is at least 1
	    if (!this.validateEloGainedNotZero(groups.get(0), groups.get(1))) {
            // Add back to the queue...let's hope this doesn't end up with some nasty system of people stuck at the bottom of the queue
            this.matchMakingService.addGroup(groups.get(0).getGroup(), groups.get(0).getRating());
            this.matchMakingService.addGroup(groups.get(1).getGroup(), groups.get(1).getRating());
		    return false;
	    }

        Arena arena;
        try {
            arena = ArenaManager.getArena(this.getKitRuleSet().getArenaType());
        } catch (OutOfArenasException e) {
            try {
                GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.matchMakingState, groups.get(0).getGroup());
            } catch (IllegalStateTransitionException ex) {
                groups.get(0).getGroup().sendMessage(ChatColor.RED + "Something broke, relog to be safe");
            }

            try {
                GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.matchMakingState, groups.get(1).getGroup());
            } catch (IllegalStateTransitionException ex) {
                groups.get(1).getGroup().sendMessage(ChatColor.RED + "Something broke, relog to be safe");
            }

            groups.get(0).getGroup().sendMessage(ChatColor.RED + "Out of arenas, re queue up.");
            groups.get(1).getGroup().sendMessage(ChatColor.RED + "Out of arenas, re queue up.");

            return false;
        }

        // Set Arena now
        match.setArena(arena);

        // Transition them into ranked or regular match state
        try {
            GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, groups.get(0).getGroup(), match);
            GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, groups.get(1).getGroup(), match);

            if (this.isRanked) {
                GroupStateMachine.regularMatchState.push(GroupStateMachine.rankedMatchState, groups.get(0).getGroup());
                GroupStateMachine.regularMatchState.push(GroupStateMachine.rankedMatchState, groups.get(1).getGroup());
            }
        } catch (IllegalStateTransitionException e) {
	        arena.toggleBeingUsed();
	        Bukkit.getLogger().info("GBERRY HALP 4");

            groups.get(0).getGroup().sendMessage(ChatColor.RED + "Internal error adding to match, contact an admin if this continues.");
            groups.get(1).getGroup().sendMessage(ChatColor.RED + "Internal error adding to match, contact an admin if this continues.");

            return false;
        }

	    Gberry.log("MATCHMAKER", "Adding" + groups.get(0).getGroup() + "and" + groups.get(1).getGroup() + " to " + this.ladderId + " with ladder type " + this.ladderType.name());
        match.prepGame(groups.get(0).getGroup(), groups.get(1).getGroup(), groups.get(0).getRating(), groups.get(1).getRating(), this);

        return true;
    }

    public boolean validateCorrectNumberOfPlayers(Group.GroupRating group1, Group.GroupRating group2) {
        if (this.isRanked && group1.getGroup().isParty()) {
            boolean validGroup1 = true, validGroup2 = true;
            if (this.ladderType == LadderType.TwoVsTwoRanked) {
                if (group1.getGroup().players().size() != 2) {
                    validGroup1 = false;
                }

                if (group2.getGroup().players().size() != 2) {
                    validGroup2 = false;
                }
            } else if (this.ladderType == LadderType.ThreeVsThreeRanked) {
                if (group1.getGroup().players().size() != 3) {
                    validGroup1 = false;
                }

                if (group2.getGroup().players().size() != 3) {
                    validGroup2 = false;
                }
            }

            if (!validGroup1 || !validGroup2) {
                if (validGroup1) {
                    this.matchMakingService.addGroup(group1.getGroup(), group1.getRating());
                }

                if (validGroup2) {
                    this.matchMakingService.addGroup(group2.getGroup(), group2.getRating());
                }

                return false;
            }
        }

        return true;
    }

	public boolean validateEloGainedNotZero(Group.GroupRating group1, Group.GroupRating group2) {
		int player1Rating = group1.getRating();
		int player2Rating = group2.getRating();

		return Math.abs(player1Rating - player2Rating) < 720;
	}

}
