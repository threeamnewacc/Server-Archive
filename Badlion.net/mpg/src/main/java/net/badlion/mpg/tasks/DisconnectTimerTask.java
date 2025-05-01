package net.badlion.mpg.tasks;

import net.badlion.combattag.CombatTagPlugin;
import net.badlion.combattag.LoggerNPC;
import net.badlion.mpg.MPGPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class DisconnectTimerTask extends BukkitRunnable {

	private MPGPlayer mpgPlayer;

	public DisconnectTimerTask(MPGPlayer mpgPlayer) {
		this.mpgPlayer = mpgPlayer;
	}

	@Override
	public void run() {
		// Remove the player's logger NPC whih will handle all the logic
		CombatTagPlugin.getInstance().getLogger(this.mpgPlayer.getUniqueId()).remove(LoggerNPC.REMOVE_REASON.TIMEOUT);
	}

}
