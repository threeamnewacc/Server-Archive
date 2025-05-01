package net.badlion.lobbychat;

import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.ChannelHandler;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LobbyChannelHandler implements ChannelHandler {

    @Override
    public void sendMessageToChannel(final Player player, String message, Channel channel) {
	    if (SmellyChat.GLOBAL_MUTE && (!player.hasPermission("badlion.staff") || player.isDisguised())) {
		    player.sendFormattedMessage("{0}Global chat is disabled", ChatColor.RED);
		    return;
	    }

	    String prefix = SmellyChat.getInstance().getGPermissions().getUserMeta(player.getUniqueId(), "prefix");
	    prefix = (prefix + ChatSettingsManager.getChatSettings(player).getGroupPrefix()).replace("&", "§");
	    final String str1 = prefix + ChatColor.BLUE + player.getDisplayName();
	    final String str2 = ": " + message;
		final String defaultMsg = str1 + ChatSettingsManager.Setting.GLOBAL_CHAT_COLOR.getValue() + str2;

	    ChatMessage defaultChatMessage = LobbyChat.getInstance().getServer().createChatMessage(defaultMsg, false);
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

	    // Post this message to the REST API
	    new BukkitRunnable() {
		    @Override
		    public void run() {
			    List<String> data = new ArrayList<>();
			    data.add(player.getUniqueId().toString());
			    data.add(StringEscapeUtils.escapeJava(str1));
			    data.add(StringEscapeUtils.escapeJava(str2));
			    data.add(StringEscapeUtils.escapeJava(defaultMsg));

			    JSONObject jsonObject = new JSONObject();
			    jsonObject.put("sync_event", data);

			    try {
				    HTTPCommon.executePOSTRequest("http://" + GetCommon.getIpForDB() + ":10123/ChatSync/" + SmellyChat.getInstance().getServerUUID().toString() + "/uTHbNCWEMhCSRagbgF724UMX2kQUJGAM", jsonObject);
			    } catch (HTTPRequestFailException e) {
				    LobbyChat.getInstance().getServer().getLogger().info("Failed to sync lobby chat message with error " + e.getResponseCode());
			    }
		    }
	    }.runTaskAsynchronously(LobbyChat.getInstance());
    }

}
