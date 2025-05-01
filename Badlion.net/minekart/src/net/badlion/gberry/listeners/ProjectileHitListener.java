package net.badlion.gberry.listeners;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import com.tinywebteam.badlion.MineKart;

public class ProjectileHitListener implements Listener {

	private MineKart plugin;
	
	public ProjectileHitListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		if (projectile instanceof Snowball) {
			Snowball snowball = (Snowball) projectile;
			// Slow target here
		} else if (projectile instanceof EnderPearl) {
			EnderPearl enderPearl = (EnderPearl) projectile;
			// Make grenade explosion here
		}
		
	}
	
}
