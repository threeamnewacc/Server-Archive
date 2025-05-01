package net.badlion.potpvp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InviteCommand extends GCommandExecutor {

	public InviteCommand() {
		super(1); // 1 args required
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		this.player.performCommand("party invite " + args[0]);
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_PURPLE + "/invite <name> - Invite <name> to party.");
	}

}
