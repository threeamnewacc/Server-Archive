package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.managers.MatchMakingManager;
import net.badlion.statemachine.State;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class LeaveCommand extends GCommandExecutor {

	public LeaveCommand() {
		super(0); // 0 arg minimum
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		if (this.group.getLeader() == this.player) {
			State<Group> state = GroupStateMachine.getInstance().getCurrentState(this.group);
			if ((GroupStateMachine.matchMakingState.contains(this.group) && state == GroupStateMachine.matchMakingState)) {
				// Try to leave matchmaking
				MatchMakingManager.removeFromMatchMaking(this.group, true);
			} else {
				this.player.sendFormattedMessage("{0}Can only use /leave when in queue.", ChatColor.RED);
			}
		} else {
			this.player.sendFormattedMessage("{0}Only the leader can perform /leave.", ChatColor.RED);
		}
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendFormattedMessage("{0}Command usage: {1}", ChatColor.RED, "/leave");
	}

}
