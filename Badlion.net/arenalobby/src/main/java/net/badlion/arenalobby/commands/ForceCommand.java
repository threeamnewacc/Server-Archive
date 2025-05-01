package net.badlion.arenalobby.commands;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
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


		Player first = ArenaLobby.getInstance().getServer().getPlayer(args[2]);
		Player second = ArenaLobby.getInstance().getServer().getPlayer(args[3]);

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

		Group group1 = ArenaLobby.getInstance().getPlayerGroup(first);
		Group group2 = ArenaLobby.getInstance().getPlayerGroup(second);

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


		if (GroupStateMachine.getInstance().getCurrentState(group1) != GroupStateMachine.lobbyState) {
			this.player.sendMessage(ChatColor.RED + "Group 1 is not in the lobby state.");
			return;
		} else if (GroupStateMachine.getInstance().getCurrentState(group2) != GroupStateMachine.lobbyState) {
			this.player.sendMessage(ChatColor.RED + "Group 2 is not in the lobby state.");
			return;
		}


        /* TODO: Send api request to start a tournament match (Not sure how tournaments worked before)

        try {
            TournamentMatch match = new TournamentMatch(kitRuleSet);

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
	        Bukkit.getLogger().info("GBERRY HALP 1");
            ArenaLobby.getInstance().somethingBroke(this.player, group1, group2);
        }*/
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Command usage: /force [group_size] [kit_rule_set] [group_1] [group_2]");
	}

}
