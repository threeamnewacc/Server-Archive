package net.badlion.kitpvp.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.Racer;

public class AttachToHorseTask extends BukkitRunnable {
	
	private Racer racer;
	
	public AttachToHorseTask(Racer racer) {
		this.racer = racer;
	}
	
	@Override
	public void run() {
		this.racer.getHorse().setPassenger(this.racer.getPlayer());
	}

}
