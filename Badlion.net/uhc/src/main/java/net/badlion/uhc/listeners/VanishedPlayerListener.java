package net.badlion.uhc.listeners;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;

public class VanishedPlayerListener implements Listener {

    public static boolean isVanishedPlayer(HumanEntity player) {
        return !player.isOp() && UHCPlayerManager.getUHCPlayer(player.getUniqueId()).isVanishedPlayer();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void playerInteract(PlayerInteractEvent event) {
        if (isVanishedPlayer(event.getPlayer())) {
	        // Let mods/host open chests
	        UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(event.getPlayer().getUniqueId());
	        /*if (uhcPlayer.getState() == UHCPlayer.State.MOD || uhcPlayer.getState() == UHCPlayer.State.HOST) {
		        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.CHEST) {
			        Chest chest = (Chest) event.getClickedBlock().getState();
			        event.getPlayer().sendMessage("OK");
			        // Clone the chest inventory
			        Inventory inventory = BadlionUHC.getInstance().getServer().createInventory(null, chest.getInventory().getSize());
			        for (ItemStack itemStack : chest.getInventory().getContents()) {
				        //inventory.addItem(itemStack); // TODO: WTF?
			        }

			        //BukkitUtil.openInventory(event.getPlayer(), inventory);

			        event.setCancelled(true);
			        event.setUseInteractedBlock(Event.Result.DENY);
			        return;
		        }
	        }*/

            if (event.getItem() == null || event.getItem().getType() != Material.COMPASS) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void entityTargetEvent(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (isVanishedPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        if (isVanishedPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeash(PlayerLeashEntityEvent event) {
        if (isVanishedPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerUnleash(PlayerUnleashEntityEvent event) {
        if (isVanishedPlayer(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {
		if (isVanishedPlayer(event.getWhoClicked())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerBreakBlock(BlockBreakEvent event) {
		if (isVanishedPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerPlaceBlock(BlockPlaceEvent event) {
		if (isVanishedPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerPickupItem(PlayerPickupItemEvent event) {
		if (isVanishedPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void playerDropItem(PlayerDropItemEvent event) {
		if (isVanishedPlayer(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
    public void hostSplashPotion(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        if (isVanishedPlayer((Player) event.getEntity().getShooter())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void hostDamageEntity(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof Projectile && ((Projectile) event.getDamager()).getShooter() instanceof Player) {
            player = (Player) ((Projectile) event.getDamager()).getShooter();
        }

        if (player != null && isVanishedPlayer(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamageVehicle(VehicleDamageEvent event) {
        Entity entity = event.getAttacker();
        if (entity != null && entity instanceof Player) {
            Player player = (Player) entity;
            if (isVanishedPlayer(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerTakeVoidDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (player.isOp()) {
                return;
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID && isVanishedPlayer(player)) {
                event.getEntity().teleport(BadlionUHC.getInstance().getSpawnLocation());
                event.setCancelled(true);
            }
        }
    }


}
