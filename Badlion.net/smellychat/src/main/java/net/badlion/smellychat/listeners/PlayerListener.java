package net.badlion.smellychat.listeners;

import net.badlion.gberry.events.GSyncEvent;
import net.badlion.smellychat.Channel;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.commands.ModChatCommand;
import net.badlion.smellychat.managers.ChannelManager;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class PlayerListener implements Listener {

	@EventHandler
	public void playerCommandEvent(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("/muteall")) {
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + "Please use the new command \"/ch settings\"");
		}
	}

	@EventHandler
	public void onGSyncGlobalMute(GSyncEvent event) {
		String subChannel = event.getArgs().get(0);
		if (subChannel.equals("ModChat")) {
			String message = StringEscapeUtils.unescapeJava(event.getArgs().get(1));

			for (Player pl : SmellyChat.getInstance().getMods()) {
				if (!ModChatCommand.modChatDisabled.contains(pl.getUniqueId())) {
					pl.sendMessage(message);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

		// Have they muted global chat?
		if (!(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT) && chatSettings.getActiveChannel().equals("G")) {
			player.sendMessage(ChatColor.RED + "You have muted chat, do \"/ch settings\" to unmute it!");
			event.setCancelled(true);
			return;
		}

		Channel channel = ChannelManager.getChannel(chatSettings.getActiveChannel());

		// Intercept it at the source
		event.setMessage(event.getMessage().replace("\u0130", "I"));
		event.setMessage(event.getMessage().replace("\u0131", "i"));

		// Send the message to the channel
		SmellyChat.getInstance().getChannelHandler().sendMessageToChannel(player, event.getMessage(), channel);

		// Always cancel
		event.setCancelled(true);
	}

}
