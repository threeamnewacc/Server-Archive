package net.badlion.gfactions.events.koth;

import net.badlion.gfactions.GFactions;
import org.bukkit.scheduler.BukkitRunnable;

public class ChangeHillTask extends BukkitRunnable {
	
	private GFactions plugin;
	
	public ChangeHillTask(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		/*KOTH koth = this.plugin.getKoth();
		if (koth != null) {
			// Make sure we get a new zone
			int oldZone = koth.getEventZone();
			int newZone = koth.getRandomGenerator().nextInt(koth.getMaxZones()); // [0-n)
			while (newZone == oldZone) {
				newZone = koth.getRandomGenerator().nextInt(koth.getMaxZones()); // [0-n)
			}
			koth.setEventZone(newZone);
			koth.changeCapZone();
		} else {
			this.cancel();
		}*/
	}

}
