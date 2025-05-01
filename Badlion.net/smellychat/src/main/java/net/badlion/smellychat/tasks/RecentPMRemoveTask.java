package net.badlion.smellychat.tasks;

import net.badlion.smellychat.managers.ChatSettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RecentPMRemoveTask extends BukkitRunnable {

	private Player sender;
	private ChatSettingsManager.ChatSettings chatSettings;

	public RecentPMRemoveTask(Player sender, ChatSettingsManager.ChatSettings chatSettings) {
		this.sender = sender;
		this.chatSettings = chatSettings;
	}

	@Override
	public void run() {
		this.chatSettings.removeRecentPM(this.sender);
	}

}
