package com.massivecraft.factions.type;

import club.minemen.hcfactions.HCFactions;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class LogoutTask {

	private final HCFactions plugin;
	private final Player player;
	private final BukkitTask task;
	private final Location location;

	@Getter private int countdown = 30;

	public LogoutTask(HCFactions plugin, Player player) {
		this.plugin = plugin;
		this.player = player;
		this.location = player.getLocation();

		this.task = Bukkit.getScheduler().runTaskTimer(plugin, this::onTimer, 0, 20);
	}

	public void cancel() {
		this.task.cancel();
		this.plugin.getLogoutTasks().remove(this.player);
	}

	private void onTimer() {
		if (this.player.isDead()) {
			this.cancel();
			return;
		}

		if (this.countdown == 0) {
			this.player.kickPlayer("Logged out safely");
			this.cancel();
			return;
		}

		Location newLocation = this.player.getLocation();
		if (newLocation.getWorld() != this.location.getWorld() || newLocation.distanceSquared(this.location) > 1.0) {
			this.cancel();
			this.player.sendFormattedMessage("{0}Logout cancelled because you moved.", ChatColor.RED);
			return;
		}

		this.player.sendFormattedMessage("{0}Logging out: {1}s", ChatColor.YELLOW, this.countdown);
		this.player.playSound(this.player.getLocation(), Sound.NOTE_BASS, 1.0f, 1.0f);
		this.countdown--;
	}

}
