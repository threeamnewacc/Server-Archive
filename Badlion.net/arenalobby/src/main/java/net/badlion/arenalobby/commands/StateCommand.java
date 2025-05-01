package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class StateCommand extends GCommandExecutor {

	public StateCommand() {
		super(1); // 1 arg minimum
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		// If they are in matchmaking and they are in a sub-state
		Group group = ArenaLobby.getInstance().getPlayerGroup(Bukkit.getPlayer(args[0]));

		State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
		if (group != null) {
			this.player.sendMessage(ChatColor.YELLOW + " " + args[0] + " is in state " + currentState.getStateName());
			List<String> states = GroupStateMachine.getInstance().debugTransitionsForElement(group);
			for (String s : states) {
				Bukkit.getLogger().info(s);
			}
		}

		for (State<Group> state : GroupStateMachine.getInstance().getStates()) {
			Bukkit.getLogger().info("===State " + state.getStateName() + "===");
			for (Group g : state.getElements()) {
				Bukkit.getLogger().info(g.toString());
			}
		}
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Command usage: /state [name]");
	}

}
