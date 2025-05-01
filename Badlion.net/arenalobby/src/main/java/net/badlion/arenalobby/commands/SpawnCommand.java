package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class SpawnCommand extends GCommandExecutor {

	public SpawnCommand() {
		super(0); // 0 arg minimum
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		// If they are in matchmaking and they are in a sub-state
		State<Group> state = GroupStateMachine.getInstance().getCurrentState(this.group);
		if (GroupStateMachine.matchMakingState.contains(this.group) && state != GroupStateMachine.matchMakingState) {
			try {
				GroupStateMachine.transitionBackToDefaultState(state, this.group);
			} catch (IllegalStateTransitionException exception) {
				this.group.sendLeaderMessage(ChatColor.RED + "You cannot /spawn at the moment because " + state.description());
				this.group.sendLeaderMessage(ChatColor.RED + "If this is an error contact an admin (i don't think this should happen -Gberry)");
			}
		} else if (GroupStateMachine.kitCreationState.contains(this.group)) {
			try {
				GroupStateMachine.transitionBackToDefaultState(state, this.group);
			} catch (IllegalStateTransitionException exception) {
				this.group.sendLeaderMessage(ChatColor.RED + "You cannot /spawn at the moment because " + state.description());
				this.group.sendLeaderMessage(ChatColor.RED + "If this is an error contact an admin (i don't think this should happen -Gberry)");
			}
		} else {
			this.player.sendFormattedMessage("{0}You can only use {1} when in a game. Use {2} to leave a queue.", ChatColor.RED, "/spawn", "/leave");
		}
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendFormattedMessage("{0}Command usage: {1}", ChatColor.RED, "/spawn");
	}

}
