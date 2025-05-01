package net.badlion.gguard.listeners;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;

public class PVPListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player || entity instanceof Horse) {
			Location location = entity.getLocation();
			ProtectedRegion region = GGuard.getInstance().getPVPProtectedRegion(location, GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowPVP()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player || entity instanceof Horse) {
			Location location = entity.getLocation();
			ProtectedRegion region = GGuard.getInstance().getPVPProtectedRegion(location, GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowPVP()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPotionSplash(PotionSplashEvent event) {
		// Stop the pvp protected person from harming others
		if (event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();
			
			// Cancel pot effects
			for (Entity entity : event.getAffectedEntities()) {
				ProtectedRegion region = GGuard.getInstance().getPVPProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowPVP()) {
                    // Allow them to affect themselves.
                    if (entity == player) {
                        continue;
                    }

					event.setIntensity((LivingEntity) entity, 0.0);
                    if (GGuard.VERBOSE) {
                        player.sendMessage(ChatColor.RED + "This is a PVP Protected Zone.");
                    }
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player || entity instanceof Horse) {
			ProtectedRegion region = GGuard.getInstance().getPVPProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowPVP()) {
				LivingEntity damager = null;
				if (event.getDamager() instanceof Projectile) {
					damager = ((Projectile) event.getDamager()).getShooter() != null && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity
									  ? (LivingEntity) ((Projectile) event.getDamager()).getShooter() : null;
				}

				if (event.getDamager() instanceof LivingEntity) {
					damager = (LivingEntity) event.getDamager();
				}

				if (damager instanceof Player) {
                    if (GGuard.VERBOSE) {
                        ((Player) damager).sendMessage(ChatColor.RED + "This is a PVP Protected Zone.");
                    }
				}

				if (event.getDamager() instanceof Projectile) {
					event.getDamager().remove();
				}

				event.setCancelled(true);
				return;
			}

			// Check where the attacker is
            LivingEntity damager = null;
            if (event.getDamager() instanceof LivingEntity) {
			    region = GGuard.getInstance().getPVPProtectedRegion(event.getDamager().getLocation(), GGuard.getInstance().getProtectedRegions());
                damager = (LivingEntity) event.getDamager();
            } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity) {
                region = GGuard.getInstance().getPVPProtectedRegion(((LivingEntity) ((Projectile) event.getDamager()).getShooter()).getLocation(), GGuard.getInstance().getProtectedRegions());
                damager = ((Projectile) event.getDamager()).getShooter() != null && ((Projectile) event.getDamager()).getShooter() instanceof LivingEntity
                                  ? (LivingEntity) ((Projectile) event.getDamager()).getShooter() : null;
            }

            // Got attacker location
			if (region != null && !region.isAllowPVP()) {
				if (damager instanceof Player) {
                    if (GGuard.VERBOSE) {
                        ((Player) damager).sendMessage(ChatColor.RED + "This is a PVP Protected Zone.");
                    }
				}

				if (event.getDamager() instanceof Projectile) {
					event.getDamager().remove();
				}

				event.setCancelled(true);
			}
		}
	}
	
	/*@EventHandler(priority=EventPriority.LOWEST)
	public void onEntityLaunchProjectile(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() != null) {
			if (event.getEntity().getShooter() instanceof LivingEntity) {
				LivingEntity entity = (LivingEntity) event.getEntity().getShooter();
				ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowPVP()) {
					if (!(event.getEntity() instanceof FishHook)) {
						event.getEntity().remove();
						event.setCancelled(true);
						if (entity instanceof Player) {
                            if (GGuard.VERBOSE) {
                                ((Player) entity).sendMessage(ChatColor.RED + "This is a PVP Protected Zone.");
                            }
						}
					}
				}
			}
		}
	}*/

}
