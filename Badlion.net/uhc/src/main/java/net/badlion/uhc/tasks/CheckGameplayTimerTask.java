package net.badlion.uhc.tasks;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckGameplayTimerTask extends BukkitRunnable {

    public CheckGameplayTimerTask() {
    }

    @Override
    public void run() {
	    BadlionUHC.getInstance().announceGameplayTime();
    }

}
