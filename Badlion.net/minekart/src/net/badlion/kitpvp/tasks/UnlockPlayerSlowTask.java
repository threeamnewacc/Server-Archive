package net.badlion.kitpvp.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Racer;

public class UnlockPlayerSlowTask extends BukkitRunnable {
	
	private MineKart plugin;
	private Racer racer;
	
	public UnlockPlayerSlowTask(MineKart plugin, Racer racer) {
		this.plugin = plugin;
		this.racer = racer;
	}
	
	@Override
	public void run() {
		racer.setLockSpeedChange(false);
	}

}
