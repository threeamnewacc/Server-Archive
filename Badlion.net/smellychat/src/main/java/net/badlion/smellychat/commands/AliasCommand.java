package net.badlion.smellychat.commands;

import net.badlion.gberry.listeners.ChatListener;
import net.badlion.smellychat.commands.handlers.ActiveCommandHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AliasCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			String identifier = s.substring(0, 1);

			StringBuilder sb = new StringBuilder();
			for (String s2 : args) {
				sb.append(s2);
				sb.append(" ");
			}

			String message = sb.toString();

			// Global channel chat filter check
			if (identifier.equalsIgnoreCase("G")) {
				if (!ChatListener.isChatMessageValid(player, message)) {
					return true;
				}
			}

			ActiveCommandHandler.handleActiveCommand(player, identifier, message);
		}

		return true;
	}

}
