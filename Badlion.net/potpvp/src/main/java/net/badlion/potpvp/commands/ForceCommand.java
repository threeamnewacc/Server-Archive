package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.Arena;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.matchmaking.TournamentMatch;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ForceCommand extends GCommandExecutor {

	private List<String> allowedArenas = new ArrayList<>();

    public ForceCommand() {
        super(3); // 1 arg minimum

//	    this.allowedArenas.add("calmforest");
//	    this.allowedArenas.add("valleyriver");
//	    this.allowedArenas.add("weirdnether");
//	    this.allowedArenas.add("wonderplains");
//	    this.allowedArenas.add("smelly");
//	    this.allowedArenas.add("savannah");
//	    this.allowedArenas.add("taiga");
//	    this.allowedArenas.add("colours");
	    this.allowedArenas.add("wasteland");
//	    this.allowedArenas.add("fallforestuhc");
    }

    @Override
    public void onGroupCommand(Command command, String label, String[] args) {
        int groupSize;

        // Check group size
        try {
            groupSize = Integer.valueOf(args[0]);

            if (groupSize < 1 && groupSize > 99) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            this.player.sendMessage(ChatColor.RED + "Invalid group size specified.");
            return;
        }

        KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(args[1]);
        if (kitRuleSet == null) {
            this.player.sendMessage(ChatColor.RED + "Invalid kit specified.");
            return;
        }


        Player first = PotPvP.getInstance().getServer().getPlayer(args[2]);
        Player second = PotPvP.getInstance().getServer().getPlayer(args[3]);

        // Check players
        if (first == null && second == null) {
            this.player.sendMessage(ChatColor.RED + args[2] + " and " + args[3] + " could not be found.");
            return;
        } else if (first == null) {
            this.player.sendMessage(ChatColor.RED + args[2] + " could not be found.");
            return;
        } else if (second == null) {
            this.player.sendMessage(ChatColor.RED + args[3] + " could not be found.");
            return;
        }

        Group group1 = PotPvP.getInstance().getPlayerGroup(first);
        Group group2 = PotPvP.getInstance().getPlayerGroup(second);

        if (group1 == group2) {
            this.player.sendMessage(ChatColor.RED + "Players are in the same group.");
            return;
        }

        int group1Size = group1.players().size();
        int group2Size = group2.players().size();

        // Check group sizes
        if (group1Size != groupSize && group2Size != groupSize) {
            this.player.sendMessage(ChatColor.RED + args[2] + " and " + args[3] + "'s group size is not " + groupSize + ".");
            return;
        } else if (group1Size != groupSize) {
            this.player.sendMessage(ChatColor.RED + args[2] + "'s group size is not " + groupSize + ".");
            return;
        } else if (group2Size != groupSize) {
            this.player.sendMessage(ChatColor.RED + args[3] + "'s group size is not " + groupSize + ".");
            return;
        }

        // Check appropriate states
        if (group1.isParty()) {
            if (GroupStateMachine.getInstance().getCurrentState(group1) != GroupStateMachine.partyState) {
                this.player.sendMessage(ChatColor.RED + "Group 1 is not in the party state.");
                return;
            } else if (GroupStateMachine.getInstance().getCurrentState(group2) != GroupStateMachine.partyState) {
                this.player.sendMessage(ChatColor.RED + "Group 2 is not in the party state.");
                return;
            }
        } else {
            if (GroupStateMachine.getInstance().getCurrentState(group1) != GroupStateMachine.lobbyState) {
                this.player.sendMessage(ChatColor.RED + "Group 1 is not in the lobby state.");
                return;
            } else if (GroupStateMachine.getInstance().getCurrentState(group2) != GroupStateMachine.lobbyState) {
                this.player.sendMessage(ChatColor.RED + "Group 2 is not in the lobby state.");
                return;
            }
        }

	    // Make sure everyone is alive
	    if (group1.hasDeadPlayers()) {
		    this.player.sendMessage(ChatColor.RED + "Group 1 has dead players, tell them to respawn.");
		    return;
	    } else if (group2.hasDeadPlayers()) {
		    this.player.sendMessage(ChatColor.RED + "Group 1 has dead players, tell them to respawn.");
		    return;
	    }

        // Get an arena
        Arena arena;
        try {
            do {
	            //arena = ArenaManager.getArena(kitRuleSet.getArenaType());
	            arena = ArenaManager.getArena(ArenaManager.ArenaType.BUILD_UHC);
            } while (!this.isAllowedArena(arena));
        } catch (OutOfArenasException e) {
            this.player.sendMessage(ChatColor.RED + "Out of arenas, try again in a few seconds.");
            return;
        }

        try {
            TournamentMatch match = new TournamentMatch(arena, kitRuleSet);

            // Transfer states
            if (group1.isParty()) {
                GroupStateMachine.partyState.transition(GroupStateMachine.matchMakingState, group1);
                GroupStateMachine.partyState.transition(GroupStateMachine.matchMakingState, group2);
            } else {
                GroupStateMachine.lobbyState.transition(GroupStateMachine.matchMakingState, group1);
                GroupStateMachine.lobbyState.transition(GroupStateMachine.matchMakingState, group2);
            }

            GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, group1, match);
            GroupStateMachine.matchMakingState.push(GroupStateMachine.regularMatchState, group2, match);

            // Now start a match
            match.prepGame(group1, group2);
            match.startGame();

            this.player.sendMessage(ChatColor.GREEN + "Match created and started.");
            Gberry.broadcastMessage(ChatColor.LIGHT_PURPLE + "Tournament match started between " + group1.toString() + " vs " + group2.toString());
        } catch (IllegalStateTransitionException e) {
	        arena.toggleBeingUsed();
	        Bukkit.getLogger().info("GBERRY HALP 1");
	        PotPvP.getInstance().somethingBroke(this.player, group1, group2);
        }
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /force [group_size] [kit_rule_set] [group_1] [group_2]");
    }

	private boolean isAllowedArena(Arena arena) {
		for (String allowedArena : this.allowedArenas) {
			if (arena.getArenaName().contains(allowedArena)) {
				return true;
			}
		}

		return false;
	}

}
