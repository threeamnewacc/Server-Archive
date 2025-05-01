package net.badlion.smellychat.listeners;

import net.badlion.banmanager.BanManager;
import net.badlion.banmanager.events.PunishedPlayerEvent;
import net.badlion.gberry.events.SettingsLoadedEvent;
import net.badlion.gpermissions.bukkitevents.GroupChangeEvent;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.events.GlobalMuteEvent;
import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class CustomEventListener implements Listener {

	public static boolean globalMuteFromDB = false;

	@EventHandler
	public void onPunishedPlayerEvent(PunishedPlayerEvent event) {
		if (event.getPunishmentType() != BanManager.PUNISHMENT_TYPE.MUTE) return;

		Player player = SmellyChat.getInstance().getServer().getPlayer(event.getUuid());

		// Is the player online on this server?
		if (player != null) {
			final ChatSettingsManager.ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

			// Disable global chat quietly
			if ((boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT)) {
				chatSettings.setSetting(ChatSettingsManager.Setting.GLOBAL_CHAT, false);
			}

			// Enable global chat in 1 minute
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!(boolean) chatSettings.getSetting(ChatSettingsManager.Setting.GLOBAL_CHAT)) {
						chatSettings.setSetting(ChatSettingsManager.Setting.GLOBAL_CHAT, true);
					}
				}
			}.runTaskLater(SmellyChat.getInstance(), 1200L);
		}
	}

    @EventHandler
    public void onSettingsLoaded(SettingsLoadedEvent event) {
        if (event.getSettings().containsKey("global_mute")) {
            SmellyChat.GLOBAL_MUTE = Boolean.valueOf(event.getSettings().get("global_mute"));
            CustomEventListener.globalMuteFromDB = SmellyChat.GLOBAL_MUTE;
        }
    }

	@EventHandler
	public void onGlobalMute(GlobalMuteEvent event) {
		if (CustomEventListener.globalMuteFromDB) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onGroupChangeEvent(GroupChangeEvent event) {
		ChatSettingsManager.getChatSettings(event.getPlayer()).updateGroupPrefix();
	}

}
