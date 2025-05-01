package net.badlion.smellychat;

import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

public class RegularChannelHandler implements ChannelHandler {

	@Override
	public void sendMessageToChannel(Player player, String message, Channel channel) {
		if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
			player.sendMessage(ChatColor.RED + "Global chat is disabled");
			return;
		}

		String prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
		prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");
		String str1 = prefix + ChatColor.BLUE + player.getDisplayName();
		String str2 = ": " + message;

		ChatMessage defaultChatMessage = SmellyChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
		for (Player pl : SmellyChat.getInstance().getServer().getOnlinePlayers()) {
			ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(pl);

			// Have they muted global chat?
			if (!(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT)) {
				continue;
			}

			// Is player ignoring the sender?
			if (!chatSettings.isIgnoring(player)) {
				// Marked player check
				ChatColor markedPlayerColor = chatSettings.getMarkedPlayerColor(player);
				if (markedPlayerColor != null) {
					pl.sendMessage(str1 + markedPlayerColor + str2);
					continue;
				}

				// Send components if their global chat color is default
				ChatColor globalChatColor = (ChatColor) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR);
				if (globalChatColor == ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue()) {
					// Send the message
					defaultChatMessage.sendTo(pl);
				} else {
					// They use a different color for global chat, send them a different chat message
					SmellyChat.getInstance().getServer().createChatMessage(str1 + globalChatColor + str2, false).sendTo(pl);
				}
			}
		}

		// Log
		SmellyChat.getInstance().logMessage(channel, player, message);
	}

}
