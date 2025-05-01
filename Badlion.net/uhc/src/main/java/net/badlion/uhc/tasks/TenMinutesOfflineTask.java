package net.badlion.uhc.tasks;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class TenMinutesOfflineTask extends BukkitRunnable {

    private UHCPlayer uhcPlayer;

    public TenMinutesOfflineTask(UHCPlayer uhcPlayer) {
        this.uhcPlayer = uhcPlayer;

        this.uhcPlayer.setAFKTimeLeft(System.currentTimeMillis());
    }

    @Override
    public void run() {
        // Check that they left and see if they left more than 300 seconds ago (5 min) [Changed to 298 to leave a buffer of error]
        // We do this check because it is possible they reconnect and disconnect multiple times
        if (this.uhcPlayer.getAFKTimeLeft() != null) {
	        System.out.println("TEN MINUTES OFFLINE FOR " + this.uhcPlayer.getUsername());

			this.uhcPlayer.setDeathTime(System.currentTimeMillis());
			BadlionUHC.getInstance().updateDeathState(this.uhcPlayer.getUUID(), this.uhcPlayer.canSpectate());
			BadlionUHC.getInstance().checkForWinners();

			LoggerNPC loggerNPC = CombatTagPlugin.getInstance().getLogger(this.uhcPlayer.getUUID());

			if (loggerNPC != null) {
				loggerNPC.remove(LoggerNPC.REMOVE_REASON.DEATH);
			}
        }
    }

}
