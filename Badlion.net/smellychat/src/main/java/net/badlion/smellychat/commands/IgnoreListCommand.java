package net.badlion.smellychat.commands;

import net.badlion.smellychat.commands.handlers.IgnoreListCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					IgnoreListCommandHandler.printIgnoreList(player);
					return true;
				}
			} else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("add")) {
					IgnoreListCommandHandler.addToIgnoreList(player, args);
					return true;
				} else if (args[0].equalsIgnoreCase("rm") || args[0].equalsIgnoreCase("remove")) {
					IgnoreListCommandHandler.removeFromIgnoreList(player, args);
					return true;
				}
			}
			this.helpMessage(((Player) sender));
		}
		return true;
	}

	private void helpMessage(Player player) {
		player.sendMessage(ChatColor.AQUA + "===Ignore List Commands===");
		player.sendMessage(ChatColor.GOLD + "/ignorelist list - List ignored players");
		player.sendMessage(ChatColor.GOLD + "/ignorelist add <player> - Ignore a player");
		player.sendMessage(ChatColor.GOLD + "/ignorelist remove/rm <player> - Unignore a player");
	}

}
