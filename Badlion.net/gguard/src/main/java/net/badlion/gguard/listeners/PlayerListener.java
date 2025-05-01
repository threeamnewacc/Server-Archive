package net.badlion.gguard.listeners;

import net.badlion.gguard.GGuard;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import java.util.Collection;

public class PlayerListener implements Listener {
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onDrinkPotion(PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.POTION) {
			ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getPlayer().getLocation(), GGuard.getInstance().getProtectedRegions());
			if (region != null && !region.isAllowPotionEffects()) {
				if (event.getPlayer() != null) {
					event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to drink here.");
				}
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onOpenChest(PlayerInteractEvent event) {
		if (event.getPlayer().isOp()) {
            return; // Don't cancel;
        }

		// Ripped from WG Line https://github.com/sk89q/worldguard/blob/master/src/main/java/com/sk89q/worldguard/bukkit/WorldGuardPlayerListener.java#L896
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material type = event.getClickedBlock().getType();
			if (type == Material.CHEST
                    || type == Material.ENDER_CHEST
	                || type == Material.JUKEBOX //stores the (arguably) most valuable item
	                || type == Material.DISPENSER
	                || type == Material.FURNACE
	                || type == Material.BURNING_FURNACE
	                || type == Material.BREWING_STAND
	                || type == Material.TRAPPED_CHEST
	                || type == Material.HOPPER
	                || type == Material.DROPPER
                    || type == Material.BEACON) {
                // Cheat and add 0.1 to fix blocks
				ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getClickedBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowChestInteraction()) {
					if (event.getPlayer() != null) {
                        if (GGuard.VERBOSE) {
						    event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use this.");
                        }
					}
					event.setUseInteractedBlock(Result.DENY);
					event.setCancelled(true);
				}
	        }
		}
	}

	@EventHandler(priority=EventPriority.MONITOR)
	public void onOpenChestFinal(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (event.getClickedBlock().getType() == Material.CHEST) {
                    // Cheat and add 0.1 to fix blocks
					ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getClickedBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
					if (region != null && region.isOverrideChestUsage()) {
						event.setCancelled(false);
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPotionHit(PotionSplashEvent event) {
		// Check to see if the affected entity is a player and they are in a protection region
		Collection<LivingEntity> entities = event.getAffectedEntities();
		for (Entity entity : entities) {
			if (entity instanceof Player) {
				ProtectedRegion region = GGuard.getInstance().getProtectedRegion(entity.getLocation(), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowPotionEffects()) {
					// Allow the potion to affect the person who threw it only
					if (event.getEntity().getShooter() instanceof LivingEntity) {
						LivingEntity shooter = (LivingEntity) event.getEntity().getShooter();
						if (!shooter.equals(entity)) {
							event.setIntensity((LivingEntity) entity, 0.0);
						}
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onItemInteract(PlayerInteractEvent event) {
        if (event.getPlayer().isOp()) {
            return; // Don't cancel;
        }

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material type = event.getClickedBlock().getType();
			if (type == Material.LEVER
	                || type == Material.STONE_BUTTON
	                || type == Material.WOOD_BUTTON
	                || type == Material.NOTE_BLOCK
	                || type == Material.REDSTONE_COMPARATOR_OFF
	                || type == Material.REDSTONE_COMPARATOR_ON
	                || type == Material.REDSTONE_LAMP_OFF
	                || type == Material.REDSTONE_LAMP_ON
	                || type == Material.WOODEN_DOOR
	                || type == Material.IRON_DOOR
	                || type == Material.TRAP_DOOR
	                || type == Material.FENCE_GATE
	                || type == Material.JUKEBOX //stores the (arguably) most valuable item
	                || type == Material.DISPENSER
	                || type == Material.FURNACE
	                || type == Material.BURNING_FURNACE
	                || type == Material.WORKBENCH
	                || type == Material.BREWING_STAND
	                || type == Material.ENCHANTMENT_TABLE
	                || type == Material.CAULDRON
	                || type == Material.BEACON
	                || type == Material.ANVIL
	                || type == Material.HOPPER
	                || type == Material.DROPPER
	                || type == Material.STONE_PLATE
	                || type == Material.WOOD_PLATE
	                || type == Material.GOLD_PLATE
	                || type == Material.IRON_PLATE) {
                // Cheat and add 0.1 to fix blocks
				ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getClickedBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowItemInteraction()) {
					if (event.getPlayer() != null) {
						if (GGuard.VERBOSE) {
							event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use this.");
						}
					}
					event.setUseInteractedBlock(Result.DENY);
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPlayerSleep(PlayerInteractEvent event) {
		if (event.getPlayer().isOp()) {
			return; // Don't cancel;
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Material type = event.getClickedBlock().getType();
			if (type == Material.BED || type == Material.BED_BLOCK) {
                // Cheat and add 0.1 to fix blocks
				ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getClickedBlock().getLocation().add(0.1, 0, 0.1), GGuard.getInstance().getProtectedRegions());
				if (region != null && !region.isAllowPlayerSleep()) {
					if (event.getPlayer() != null) {
						event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use this.");
					}
					event.setUseInteractedBlock(Result.DENY);
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerEnterBed(PlayerBedEnterEvent event) {
		if (event.getPlayer().isOp()) {
			return; // don't cancel;
		}

		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getPlayer().getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowPlayerSleep()) {
			if (event.getPlayer() != null) {
				event.getPlayer().sendMessage(ChatColor.RED + "Not allowed to use this.");
			}
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		if (event.getPlayer().isOp()) {
			return; // don't cancel;
		}

		ProtectedRegion region = GGuard.getInstance().getProtectedRegion(event.getPlayer().getLocation(), GGuard.getInstance().getProtectedRegions());
		if (region != null && !region.isAllowPlayerPickupItems()) {
			event.setCancelled(true);
		}
	}

}
