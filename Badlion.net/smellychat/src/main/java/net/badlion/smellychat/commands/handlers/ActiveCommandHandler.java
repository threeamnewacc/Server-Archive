package net.badlion.smellychat.commands.handlers;

import net.badlion.smellychat.Channel;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChannelManager;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ActiveCommandHandler {

	// Used for /<identifier>c (c comes after the letter, so like /gc)
	public static void handleActiveCommand(Player player, String identifier, String message) {
		// Global mute check
		if (SmellyChat.GLOBAL_MUTE && identifier.equalsIgnoreCase("G")) {
			player.sendMessage(ChatColor.RED + "Global chat is disabled");
			return;
		}

		Channel channel = ChannelManager.getChannel(identifier.toUpperCase());

		if (channel == null) {
			throw new RuntimeException("Channel with identifier " + identifier + " not found");
		}

		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);
		if (!message.isEmpty()) {
			SmellyChat.getInstance().getChannelHandler().sendMessageToChannel(player, message, channel);
		} else {
			if (!chatSettings.isActiveChannel(channel.getIdentifier())) {
				// Set this as their active channel now
				chatSettings.setActiveChannel(channel.getIdentifier());

				player.sendMessage(ChatColor.WHITE + "You are now talking in " + channel.getColor() + "[" + channel.getIdentifier() + "] " + channel.getName() + ChatColor.WHITE + ".");
			} else {
				player.sendMessage(ChatColor.WHITE + "You are already talking in " + channel.getColor() + "[" + channel.getIdentifier() + "] " + channel.getName() + ChatColor.WHITE + ".");
			}
		}
	}

	// Used for /ch <identifier>
	public static void handleChannelActiveCommand(Player player, String identifier) {
		Channel channel = ChannelManager.getChannel(identifier.toUpperCase());
		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

		if (!chatSettings.isActiveChannel(channel.getIdentifier())) {
			// Set this as their active channel now
			chatSettings.setActiveChannel(channel.getIdentifier());

			player.sendMessage(ChatColor.WHITE + "You are now talking in " + channel.getColor() + "[" + channel.getIdentifier() + "] " + channel.getName() + ChatColor.WHITE + ".");
		} else {
			player.sendMessage(ChatColor.WHITE + "You are already talking in " + channel.getColor() + "[" + channel.getIdentifier() + "] " + channel.getName() + ChatColor.WHITE + ".");
		}
	}

}
