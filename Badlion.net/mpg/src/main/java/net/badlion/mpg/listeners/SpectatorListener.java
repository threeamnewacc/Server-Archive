package net.badlion.mpg.listeners;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.inventories.SkullPlayerInventory;
import net.badlion.mpg.inventories.SpectatorInventory;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.MatchmakingMCPListener;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpectatorListener implements Listener {

	private static Map<UUID, Long> playerWarpTimes = new HashMap<>();

	@EventHandler(priority= EventPriority.LOWEST)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		if (!player.spigot().getCollidesWithEntities()) {
			// Always cancel this if not op
			if (!player.isOp()) {
				event.setCancelled(true);
			}

			if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
				switch (event.getItem().getType()) {
					case WATCH:
						SkullPlayerInventory.openSpectatePlayerInventory(player);
						break;
					case POTION:
						SpectatorInventory.openSpeedPlayerInventory(player);
						break;
					case REDSTONE:
						MPGPlayerManager.getMPGPlayer(player.getUniqueId()).setState(MPGPlayer.PlayerState.PLAYER);
						break;
					case NAME_TAG:
						if (!MPG.USES_MATCHMAKING) break;

						Long lastTime = SpectatorListener.playerWarpTimes.get(player.getUniqueId());
						if (lastTime == null || lastTime + 5000 < System.currentTimeMillis()) {
							SpectatorListener.playerWarpTimes.put(player.getUniqueId(), System.currentTimeMillis());

							BukkitUtil.runTaskAsync(new Runnable() {
								@Override
								public void run() {
									List<Player> list = new ArrayList<>();
									list.add(player);

									MatchmakingMCPListener.mpgLobbyServerSender.sendPlayersToLobby(list);
								}
							});
						}
						break;
				}
			}
		}
	}

    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            if (event.getItemDrop() != null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
	        event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

	    if (player.isOp()) {
		    return;
	    }

        if (player.getGameMode() == GameMode.CREATIVE || !player.spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDragEvent(InventoryDragEvent event) {
        final Player player = (Player) event.getWhoClicked();
        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
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

    @EventHandler(priority = EventPriority.FIRST)
    public void onSpectatorDamageEvent(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            // No spectator damage
            Player player = (Player) event.getDamager();
            if (player.isOp()) {
                return;
            }

            // Fail safety checks
            if (!player.spigot().getCollidesWithEntities() || ((Player) event.getDamager()).getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LAST)
    public void onOpenChestFinalEvent(PlayerInteractEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.CHEST) {
                if (!event.getPlayer().spigot().getCollidesWithEntities()) {
                    event.setCancelled(true);
                }
            }
        }
    }

	@EventHandler
	public void onSpectatorTakeDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(event.getEntity().getUniqueId());
			if (mpgPlayer.getState() == MPGPlayer.PlayerState.SPECTATOR) {
				// Teleport to spectator location
				event.getEntity().teleport(MPG.getInstance().getMPGGame().getWorld().getSpectatorLocation());
			}
		}
	}

    @EventHandler
    public void onEntityTargetEvent(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player) {
            Player player = (Player) event.getTarget();
            if (player.isOp()) {
                return;
            }

            if (!player.spigot().getCollidesWithEntities()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeashEntityEvent(PlayerLeashEntityEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerUnleashEntityEvent(PlayerUnleashEntityEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDamageEvent(VehicleDamageEvent event) {
        Entity entity = event.getAttacker();
        if (entity != null && entity instanceof Player) {
            Player player = (Player) entity;

            if (player.isOp()) {
                return;
            }

            if (!player.spigot().getCollidesWithEntities()) {
                event.setCancelled(true);
            }
        }
    }

}
