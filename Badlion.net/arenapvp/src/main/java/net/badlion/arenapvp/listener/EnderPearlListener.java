package net.badlion.arenapvp.listener;

import net.badlion.arenapvp.manager.EnderPearlManager;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class EnderPearlListener implements Listener {

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
			Player player = (Player) projectile.getShooter();
			if (projectile instanceof EnderPearl) {
				EnderPearl enderPearl = (EnderPearl) projectile;
				EnderPearlManager.put(player, enderPearl);
			}
		}
	}

}
