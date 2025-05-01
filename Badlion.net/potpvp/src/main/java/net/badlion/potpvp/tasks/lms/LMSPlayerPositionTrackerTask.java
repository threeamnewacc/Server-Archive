package net.badlion.potpvp.tasks.lms;

import net.badlion.potpvp.events.LastManStanding;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LMSPlayerPositionTrackerTask extends BukkitRunnable {
	
	private LastManStanding match;
	
	public LMSPlayerPositionTrackerTask(LastManStanding match) {
		this.match = match;
	}
	
	@Override
	public void run() {
		if (this.match != null) {
			for (Player player : this.match.getPlayers()) {
				for (Player player2 : this.match.getPlayers()) {
					if (player2.equals(player)) {
						continue;
					}
					player.sendMessage(ChatColor.DARK_AQUA + "Player located at " + player2.getLocation().getBlockX() + ", " + player2.getLocation().getBlockY() + ", " +player2.getLocation().getBlockZ());
				}
			}
		}
	}

}
