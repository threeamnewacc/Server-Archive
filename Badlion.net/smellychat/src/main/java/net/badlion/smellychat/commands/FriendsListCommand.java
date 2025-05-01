package net.badlion.smellychat.commands;

import net.badlion.smellychat.commands.handlers.FriendsListCommandHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendsListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) {
					FriendsListCommandHandler.printFriendsList(player);
					return true;
				}
			}
			this.helpMessage(((Player) sender));
		}
		return true;
	}

	private void helpMessage(Player player) {
		player.sendMessage(ChatColor.AQUA + "===Friends List Commands===");
		player.sendMessage(ChatColor.GOLD + "/friendslist list - List friends");
	}

}
