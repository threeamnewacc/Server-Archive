package net.badlion.gguard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;

public class CreatureListener implements Listener {
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowCreatureSpawn()) {
			event.setCancelled(true);
		}
	}
	
}
	