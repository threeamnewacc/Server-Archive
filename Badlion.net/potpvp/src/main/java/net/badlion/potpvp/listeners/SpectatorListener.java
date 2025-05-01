package net.badlion.potpvp.listeners;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.SpectatorHelper;
import net.badlion.potpvp.inventories.spectator.SpectateEventInventory;
import net.badlion.potpvp.inventories.spectator.SpectateFFAInventory;
import net.badlion.potpvp.inventories.spectator.SpectateTDMInventory;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpectatorListener extends BukkitUtil.Listener {

	@EventHandler(ignoreCancelled = true)
	public void playerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Group group = PotPvP.getInstance().getPlayerGroup(player);
        if (!player.spigot().getCollidesWithEntities()) {
			ItemStack item = event.getItem();

			// Cancel this if not op
            if (!player.isOp()) {
                event.setCancelled(true);
	            event.setUseInteractedBlock(Event.Result.DENY);
            }

	        // Do this check after we cancel the event
	        if (item == null || item.getType().equals(Material.AIR)) return;

			if (GroupStateMachine.spectatorState.contains(group)) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    switch (player.getInventory().getHeldItemSlot()) {
	                    case 2:
		                    if (PotPvP.getInstance().isTournamentMode()) {
			                    event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, FFA is disabled.");
			                    return;
		                    }

		                    SpectateFFAInventory.openSpectateFFAInventory(player);
		                    break;
	                    case 3:
		                    if (PotPvP.getInstance().isTournamentMode()) {
			                    event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, events are disabled.");
			                    return;
		                    }

		                    SpectateEventInventory.openSpectateEventInventory(player);
		                    break;
	                    case 4:
		                    if (PotPvP.getInstance().isTournamentMode()) {
			                    event.getPlayer().sendMessage(ChatColor.RED + "Tournament mode is enabled, TDM is disabled.");
			                    return;
		                    }

		                    SpectateTDMInventory.openSpectateTDMInventory(player);
		                    break;
                        case 8:
                            SpectatorHelper.deactivateSpectateGameMode(group);
                            break;
                    }

	                event.setCancelled(true);
	                event.setUseInteractedBlock(Event.Result.DENY);
                }
			}
		}
	}

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHorseInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority= EventPriority.LOW)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (!player.spigot().getCollidesWithEntities()  && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onMoveItem(InventoryMoveItemEvent event) {
        if (event.getDestination().getType() == InventoryType.PLAYER) {
            List<HumanEntity> entities = event.getSource().getViewers();
            if (entities.get(0) instanceof Player) {
                Player player = (Player) entities.get(0);
                if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
                    event.setCancelled(true);
                }
            }
        }
    }

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();

            if (player.isOp()) {
                return;
            }

            // Only allow them to pot themselves up and not debuff nearby players
            if (!player.spigot().getCollidesWithEntities()) {
                for (Entity entity : event.getAffectedEntities()) {
                    // Allow them to affect themselves.
                    if (entity == player) {
                        continue;
                    }

                    event.setIntensity((LivingEntity) entity, 0.0);
                }
            } else {
                // Someone who is not a specator threw a potion down, don't let spectators get affected
                for (LivingEntity entity : event.getAffectedEntities()) {
                    if (entity instanceof Player) {
                        Player p = (Player) entity;
                        if (!p.spigot().getCollidesWithEntities() && !p.isOp()) {
                            event.setIntensity(entity, 0D);
                        }
                    }
                }
            }
        }
	}

    @EventHandler(priority=EventPriority.FIRST)
    public void onSpectatorDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Player player = null;
        if (damager instanceof Projectile) {
            player = ((Projectile) damager).getShooter() != null && ((Projectile) damager).getShooter() instanceof Player ? (Player) ((Projectile) damager).getShooter() : null;
        } else if (damager instanceof Player) {
            player = (Player) damager;
        }

        if (player == null || player.isOp()) {
            return;
        }

        // Fail safety checks
        if (!player.spigot().getCollidesWithEntities() || player.getGameMode() == GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerTakesDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // If they are invisible cancel damage
            if (!player.spigot().getCollidesWithEntities()) {
                event.setCancelled(true);
            }
        }
    }

}
