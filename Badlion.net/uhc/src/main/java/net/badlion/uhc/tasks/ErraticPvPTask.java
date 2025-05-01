package net.badlion.uhc.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.uhc.BadlionUHC;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class ErraticPvPTask extends BukkitRunnable {

	public static int taskId;

	@Override
	public void run() {
		// Toggle PvP
		BadlionUHC.getInstance().setPVP(!BadlionUHC.getInstance().isPVP());
		Gberry.broadcastMessage(
				ChatColor.DARK_RED + ChatColor.BOLD.toString()
						+ "PvP has been "
						+ (BadlionUHC.getInstance().isPVP() ? "enabled" : "disabled")
						+ " by Erratic PvP!"
		);

		// Min 1 minute, max 3 minutes, any time in between :D
		ErraticPvPTask.taskId = new ErraticPvPTask().runTaskLater(BadlionUHC.getInstance(), Gberry.generateRandomInt(20 * 60, 20 * 60 * 3)).getTaskId();
	}
}
