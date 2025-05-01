package net.badlion.gguard.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;

public class HangingItemListener implements Listener {
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onHangingItemBreak(HangingBreakEvent event) {
		Entity entity = event.getEntity();
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowHangingItems()) {
			if (event instanceof HangingBreakByEntityEvent) {
				if (((HangingBreakByEntityEvent) event).getRemover() instanceof LivingEntity) {
					LivingEntity remover = (LivingEntity) ((HangingBreakByEntityEvent) event).getRemover();
					if (remover instanceof Projectile) {
						remover = ((Projectile)remover).getShooter() != null ? (LivingEntity) ((Projectile)remover).getShooter() : remover;
					}

					if (remover instanceof Player) {
						if (((Player) remover).isOp()) {
							return; // Don't cancel
						}

						((Player) remover).sendMessage(ChatColor.RED + "Not allowed to break this.");
					}
				}
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onHangingItemPlace(HangingPlaceEvent event) {
		Entity entity = event.getEntity();
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowHangingItems()) {
			if (event.getPlayer() != null) {
                if (event.getPlayer().isOp()) {
                    return; // Don't cancel
                }

				event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to place that here.");
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onHangingItemPlace(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowHangingItems() && (entity instanceof ItemFrame || entity instanceof Painting)) {
			if (event.getPlayer() != null) {
                if (event.getPlayer().isOp()) {
                    return; // Don't cancel
                }

				event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to interact with that.");
			}
			event.setCancelled(true);
		}
	}

    @EventHandler(priority=EventPriority.LOWEST)
    public void onItemFrameDamage(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof ItemFrame) {
            ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
            if (region != null && !region.isAllowHangingItems()) {
	            if (event.getDamager() instanceof Player) {
		            Player player = (Player) event.getDamager();
		            if (player.isOp()) {
			            return; // Don't cancel
		            }

		            player.sendMessage(ChatColor.RED + "Not allowed to interact with that.");
	            }

                event.setCancelled(true);
            }
        }
    }

}
