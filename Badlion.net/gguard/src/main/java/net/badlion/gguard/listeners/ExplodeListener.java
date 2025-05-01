package net.badlion.gguard.listeners;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

import java.util.List;

public class ExplodeListener implements Listener {

	@EventHandler(priority=EventPriority.MONITOR)
	public void onExplosionPrime(ExplosionPrimeEvent event) {
		Location location = event.getEntity().getLocation();
		// TNT protection in protected areas
		if (event.getEntityType() == EntityType.PRIMED_TNT || event.getEntityType() == EntityType.MINECART_TNT) {
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location, GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowTNTExplosions()) {
				event.setCancelled(true);
			}
		} else if (event.getEntityType() == EntityType.CREEPER) {
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(location, GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowCreeperExplosions()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onEntityExplosion(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		List<Block> blocks = event.blockList();

		if (entity instanceof Creeper) {
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getLocation(), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowCreeperExplosions()) {
				event.setCancelled(true);
				event.blockList().clear();
				return;
			}

			// Protect the blocks that exploded if need be
			for (Block block : blocks) {
                // Cheat and add 0.1 to fix blocks
				region = GGuard.getInstance().getProtectedRegion(block.getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowCreeperBlockDamage()) {
					// Lazy way of doing it, if the explosion blocks touch any protected region, protect everything
					event.blockList().clear();
					return;
				}
			}
		}

		if (entity instanceof TNTPrimed || entity instanceof ExplosiveMinecart || entity instanceof  LargeFireball || entity instanceof Fireball) {
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getLocation(), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowTNTExplosions()) {
				event.setCancelled(true);
				event.blockList().clear();
				return;
			}

			// Protect the blocks that exploded if need be
			for (Block block : blocks) {
                // Cheat and add 0.1 to fix blocks
				region = GGuard.getInstance().getProtectedRegion(block.getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowTNTBlockDamage()) {
					// Lazy way of doing it, if the explosion blocks touch any protected region, protect everything
					event.blockList().clear();
					return;
				}
			}
		}
	}

}
