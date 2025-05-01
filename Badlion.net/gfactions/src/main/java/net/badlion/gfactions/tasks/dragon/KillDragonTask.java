package net.badlion.gfactions.tasks.dragon;

import net.badlion.gfactions.GFactions;
import org.bukkit.scheduler.BukkitRunnable;

public class KillDragonTask  extends BukkitRunnable {
	
	private GFactions plugin;
	
	public KillDragonTask(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		this.plugin.getDragonEvent().endEvent(false, null, null);
	}

}
