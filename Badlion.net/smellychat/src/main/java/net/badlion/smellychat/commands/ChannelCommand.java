package net.badlion.smellychat.commands;

import net.badlion.smellychat.Channel;
import net.badlion.smellychat.commands.handlers.ActiveCommandHandler;
import net.badlion.smellychat.managers.ChannelManager;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChannelCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("list")) { // List all channels
					player.sendMessage(ChatColor.BOLD + "All Channels:");
					for (Channel channel : ChannelManager.getChannels()) {
						player.sendMessage(ChatColor.GRAY + "- " + channel.getColor() + "[" + channel.getIdentifier() + "] " + channel.getName());
					}
				} else if (args[0].equalsIgnoreCase("setting") || args[0].equalsIgnoreCase("settings")) { // Settings
					ChatSettingsManager.SettingsInventory.openSettingsInventory(player);
				} else if (ChannelManager.getChannel(args[0].toUpperCase()) != null) {
					ActiveCommandHandler.handleChannelActiveCommand(player, args[0]);
				} else {
					this.helpMessage(player);
				}
			} else {
				this.helpMessage(player);
			}
		}
		return true;
	}

	private void helpMessage(Player player) {
		player.sendMessage(ChatColor.AQUA + "===Chat Commands===");
		player.sendMessage(ChatColor.GOLD + "/ch list - Shows all channels");
		player.sendMessage(ChatColor.GOLD + "/ch <channel> - Switch focus to specified channel");
		player.sendMessage(ChatColor.GOLD + "/ch settings - Manage chat settings");
	}

}
