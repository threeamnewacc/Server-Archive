package net.badlion.uhc.tasks;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.scheduler.BukkitRunnable;

public class PermanentDayTask extends BukkitRunnable {

	@Override
	public void run() {
		BadlionUHC.getInstance().getServer().getWorld(BadlionUHC.UHCWORLD_NAME).setTime(6000L);
	}

}
