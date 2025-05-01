package net.badlion.gberry.listeners;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.tinywebteam.badlion.MineKart;

public class EnderCrystalListener implements Listener {

	private MineKart plugin;
	
	public EnderCrystalListener(MineKart plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityBoom(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof EnderCrystal) {
			event.setCancelled(true);
		}
	}
	
}
