package net.badlion.mpglobby.tasks;

import net.badlion.mpglobby.MPGLobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class VoidCheckerTask extends BukkitRunnable {

	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getLocation().getY() < 20) {
				player.teleport(MPGLobby.getInstance().getSpawnLocation());
			}
		}
	}

}
