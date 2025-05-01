package net.badlion.factionchat;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;

public class FactionChannelHandler implements ChannelHandler {

    @Override
    public void sendMessageToChannel(Player player, String message, Channel channel) {
	    Faction faction = FPlayers.i.get(player).getFaction();

	    switch (channel.getIdentifier()) {
		    case "f":
		    case "F":
			    if (faction == null || faction.getId().equals("0")) {
				    player.sendMessage(ChatColor.RED + "You are not in a faction!");

				    // They might've just disbanded/left a faction, set global as active channel
				    ChatSettingsManager.getChatSettings(player).setActiveChannel("G");

				    return;
			    }

			    String fullMessage = ChatColor.GREEN + "[F]" + player.getName() + ": " + message;

			    ChatMessage chatMessage = FactionChat.getInstance().getServer().createChatMessage(fullMessage, false);
			    for (Player pl : faction.getOnlinePlayers()) {
				    // Send the message
				    chatMessage.sendTo(pl);
			    }
			    break;
		    default:
			    if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
				    player.sendMessage(ChatColor.RED + "Global chat is disabled");
				    return;
			    }

			    String prefix = SmellyChat.getInstance().getGPermissions().getUserGroupMetaPrefix(player.getUniqueId());
			    prefix = prefix.replace("&", "§");

			    // Permissions hardcode for factions
			    if (!player.hasPermission("badlion.staff") && !player.hasPermission("badlion.famous")
					    && !player.hasPermission("badlion.youtube") && !player.hasPermission("badlion.twitch")) {
				    if (player.hasPermission("badlion.lion")) {
					    prefix = prefix + ChatColor.GOLD + "[Lion]";
				    } else if (player.hasPermission("badlion.donatorplus")) {
					    prefix = prefix + ChatColor.GOLD + "[D+]";
				    } else if (player.hasPermission("badlion.donator")) {
					    prefix = prefix + ChatColor.GOLD + "[D]";
				    }
			    }

			    String factionPrefix = "";
			    if (faction != null && !faction.getId().equals("0")) {
				    factionPrefix = ChatColor.RED + "[" + faction.getTag() + "]";
			    }

			    String str1 = factionPrefix + prefix + ChatColor.BLUE + player.getDisplayName();
			    String str2 = ": " + message;

			    ChatMessage defaultChatMessage = FactionChat.getInstance().getServer().createChatMessage(str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2, false);
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
