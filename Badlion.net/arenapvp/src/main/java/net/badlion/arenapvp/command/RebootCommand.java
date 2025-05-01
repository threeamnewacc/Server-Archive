package net.badlion.arenapvp.command;

import net.badlion.arenapvp.listener.MCPListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RebootCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (commandSender instanceof Player) {
			if (!commandSender.isOp()) {
				commandSender.sendFormattedMessage("{0}No Permission.", ChatColor.RED);
				return false;
			}
		}

		MCPListener.shutdown = true;
		commandSender.sendMessage("Sending ready for shutdown message.");
		return false;
	}
}
