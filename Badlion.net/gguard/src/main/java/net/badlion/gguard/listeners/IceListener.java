package net.badlion.gguard.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;

public class IceListener implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onIceMelt(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (block.getType() == Material.ICE || block.getType() == Material.SNOW || block.getType() == Material.SNOW_BLOCK) {
			Location location = block.getLocation();
            // Cheat and add 0.1 to fix blocks
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location.add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowIceMelt()) {
				event.setCancelled(true);
			}
		}
	}
	
}
