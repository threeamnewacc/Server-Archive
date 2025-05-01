package net.badlion.potpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class PromoteCommand extends GCommandExecutor {

	public PromoteCommand() {
		super(1); // 1 args required
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		this.player.performCommand("party promote " + args[0]);
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_PURPLE + "/promote <name> - Promote <name> to party leader.");
	}

}
