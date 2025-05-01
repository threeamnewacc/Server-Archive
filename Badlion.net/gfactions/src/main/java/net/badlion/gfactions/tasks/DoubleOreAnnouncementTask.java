package net.badlion.gfactions.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.commands.DoubleOresCommand;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class DoubleOreAnnouncementTask extends BukkitRunnable {

	private String message = ChatColor.RED + "[" + ChatColor.BLUE + "TIP" + ChatColor.RED + "] " + ChatColor.GREEN + "Double ores are enabled, 2x ore drops!";

	@Override
	public void run() {
		if (DoubleOresCommand.doubleOresActivated) {
			Gberry.broadcastMessage(this.message);
		}
	}

}
