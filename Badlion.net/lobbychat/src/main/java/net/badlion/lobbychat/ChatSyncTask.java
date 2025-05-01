package net.badlion.lobbychat;

import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;

public class ChatSyncTask extends BukkitRunnable {

	@Override
	public void run() {
		JSONObject response;

		try {
			response = HTTPCommon.executeGETRequest("http://" + GetCommon.getIpForDB() + ":10123/ChatSync/" + SmellyChat.getInstance().getServerUUID().toString() + "/uTHbNCWEMhCSRagbgF724UMX2kQUJGAM");
		} catch (HTTPRequestFailException e) {
			Bukkit.getLogger().info("Failed to get lobby chat sync information with error code " + e.getResponseCode());
			return;
		}

		if (response != null) {
			final List<List<String>> syncMsgs = (List<List<String>>) response.get("sync_queue");

			new BukkitRunnable() {
				@Override
				public void run() {
					for (List<String> syncMsg : syncMsgs) {
						final UUID uuid = UUID.fromString((syncMsg.get(0)));
						final String str1 = StringEscapeUtils.unescapeJava(syncMsg.get(1));
						final String str2 = StringEscapeUtils.unescapeJava(syncMsg.get(2));
						final String defaultMsg = StringEscapeUtils.unescapeJava(syncMsg.get(3));
						final ChatMessage defaultChatMessage = LobbyChat.getInstance().getServer().createChatMessage(defaultMsg, false);

						for (Player pl : SmellyChat.getInstance().getServer().getOnlinePlayers()) {
							ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(pl);

							// Have they muted global chat?
							if (!(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT)) {
								continue;
							}

							// Is player ignoring the sender?
							if (!chatSettings.isIgnoring(uuid)) {
								// Marked player check
								ChatColor markedPlayerColor = chatSettings.getMarkedPlayerColor(uuid);
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
					}
				}
			}.runTask(Gberry.plugin);


			boolean b = SmellyChat.GLOBAL_MUTE;

			SmellyChat.GLOBAL_MUTE = (Boolean) response.get("global_mute");

			// Did global mute actually change? (instead of just syncing)
			if (b != SmellyChat.GLOBAL_MUTE) {
				// Broadcast global mute message
				if (SmellyChat.GLOBAL_MUTE) {
					Gberry.broadcastMessage(ChatColor.AQUA + "Global mute is now enabled!");
				} else {
					Gberry.broadcastMessage(ChatColor.AQUA + "Global mute is now disabled!");
				}
			}
		}
	}

}
