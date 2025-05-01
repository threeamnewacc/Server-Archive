package net.badlion.kitpvp.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.Racer;

public class AllowItemPickupTask extends BukkitRunnable {
	
	private Racer racer;
	
	public AllowItemPickupTask(Racer racer) {
		this.racer = racer;
	}
	
	@Override
	public void run() {
		this.racer.setAllowedToPickupItem(true);
	}

}
