package com.massivecraft.factions.listeners;

import club.minemen.hcfactions.HCFactions;
import com.massivecraft.factions.type.ChatCallback;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatCallbackListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		final String message = event.getMessage();
		final ChatCallback callback = HCFactions.getInstance().getChatCallbackManager().takeChatCallback(event.getPlayer());
		if (callback != null) {
			event.setCancelled(true);
			new BukkitRunnable() {
				@Override
				public void run() {
					callback.run(message);
				}
			}.runTask(HCFactions.getInstance());
		}
	}
}
