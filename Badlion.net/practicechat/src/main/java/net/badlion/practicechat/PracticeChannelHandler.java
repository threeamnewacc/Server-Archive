package net.badlion.practicechat;

import io.kohi.kpractice.PracticePlugin;
import io.kohi.kpractice.type.Party;
import io.kohi.kpractice.type.PlayerData;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PracticeChannelHandler implements ChannelHandler {

    @Override
    public void sendMessageToChannel(Player player, String message, Channel channel) {
		Party party = PracticePlugin.getInstance().getPartyManager().getParty(player);

	    switch (channel.getIdentifier()) {
		    case "p":
		    case "P":
			    if (party == null) {
				    player.sendMessage(ChatColor.RED + "You are not in a party!");
				    return;
			    }

			    String fullMessage;
				if (party.getName().equals(player.getName())) {
					fullMessage = ChatColor.RED + "*" + ChatColor.GRAY + "(" + ChatColor.BLUE + "Party" + ChatColor.GRAY + ")" + ChatColor.GREEN + player.getDisplayName() + ChatColor.RESET + ": " + message;
				} else {
					fullMessage = ChatColor.GRAY + "(" + ChatColor.BLUE + "Party" + ChatColor.GRAY + ")" + ChatColor.GREEN + player.getDisplayName() + ChatColor.RESET + ": " + message;
				}

			    ChatMessage chatMessage = PracticeChat.getInstance().getServer().createChatMessage(fullMessage, false);
			    for (UUID uuid : party.getPlayers()) {
					Player pl = Bukkit.getPlayer(uuid);
					if (pl != null) {
						// Send the message
						chatMessage.sendTo(pl);
					}
			    }
			    break;
		    default:
			    if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
				    player.sendMessage(ChatColor.RED + "Global chat is disabled");
				    return;
			    }

				PlayerData playerData = PracticePlugin.getInstance().getPlayerDataManager().get(player);
				if (playerData.getState() == PlayerData.PlayerState.SPECTATE) {
					player.sendMessage(ChatColor.RED + "You can not chat while spectating.");
					return;
				}

			    String prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
			    prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");
			    String str1 = prefix + ChatColor.GREEN + player.getDisplayName();
			    String str2 = ": " + message;

			    ChatMessage defaultChatMessage = PracticeChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
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
			    break;
	    }

	    // Log
	    SmellyChat.getInstance().logMessage(channel, player, message);
    }

}
