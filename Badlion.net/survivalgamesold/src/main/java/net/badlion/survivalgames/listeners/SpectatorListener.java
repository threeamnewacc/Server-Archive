package net.badlion.survivalgames.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.inventories.ServerSelectorInventory;
import net.badlion.survivalgames.inventories.SkullPlayerInventory;
import net.badlion.survivalgames.managers.SGPlayerManager;
import org.bukkit.ChatColor;
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
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpectatorListener implements Listener {

    public static ConcurrentHashMap<String, Long> playersBeingWarpedMap = new ConcurrentHashMap<String, Long>();

    @EventHandler(priority= EventPriority.LOWEST)
    public void playerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
	    if (!player.spigot().getCollidesWithEntities()) {
	        if (event.getItem() != null) {
		        if (event.getItem().getType() == Material.COMPASS) {
			        return;
		        } else if (event.getItem().getType() == Material.WATCH) {
			        SkullPlayerInventory.openSpectatePlayerInventory(player);
			        return;
		        } else if (event.getItem().getType() == Material.ENCHANTED_BOOK) {
                    player.openInventory(ServerSelectorInventory.unrankedSGInventory);
                    return;
                } else if (event.getItem().getType() == Material.FISHING_ROD) {
                    Long lastTime = playersBeingWarpedMap.get(player.getName());
                    if (lastTime == null || lastTime + 5000 < System.currentTimeMillis()) {
                        String server = Gberry.serverName.toLowerCase().startsWith("rsg") ? "sglobby" : "eusglobby";

                        player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Connecting...");
                        SurvivalGames.getInstance().sendPlayerToServer(player, server);
                        playersBeingWarpedMap.put(player.getName(), System.currentTimeMillis());
                    }
                }
	        }

	        // Always cancel this if not op
	        if (!player.isOp()) {
		        event.setCancelled(true);
	        }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!player.spigot().getCollidesWithEntities() && !player.isOp()) {
            if (event.getItemDrop() != null) {
                event.setCancelled(true);
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

    @EventHandler(priority= EventPriority.LOWEST)
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

    @EventHandler(priority=EventPriority.FIRST)
    public void onSpectatorDamage(EntityDamageByEntityEvent event) {
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

    @EventHandler(priority=EventPriority.HIGH)
    public void onSpectatorJoin(PlayerJoinEvent event) {
        SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(event.getPlayer().getUniqueId());

        // Repetitive but forces everything like vanish/items
        if (sgPlayer.getState() == SGPlayer.State.SPECTATOR) {
            sgPlayer.setState(SGPlayer.State.SPECTATOR);
        }
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onOpenChestFinal(PlayerInteractEvent event) {
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
    public void inventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Long lastTime = playersBeingWarpedMap.get(player.getName());
        if (event.getInventory().getName().equals(ServerSelectorInventory.unrankedSGInventory.getName())) {
            event.setCancelled(true);
            if (lastTime == null || lastTime + 5000 < System.currentTimeMillis()) {
                ItemStack item = event.getCurrentItem();

                if (item == null || item.getType() == Material.AIR) return;

                // Find the corresponding server
                String name = item.getItemMeta().getDisplayName();

                player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Connecting...");
                SurvivalGames.getInstance().sendPlayerToServer(player, name);
                playersBeingWarpedMap.put(player.getName(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void entityTargetEvent(EntityTargetEvent event) {
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
    public void onPlayerInteractWithEntity(PlayerInteractEntityEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeash(PlayerLeashEntityEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerUnleash(PlayerUnleashEntityEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerPickupItem(PlayerPickupItemEvent event) {
        if (event.getPlayer().isOp()) {
            return;
        }

        if (!event.getPlayer().spigot().getCollidesWithEntities()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDamageVehicle(VehicleDamageEvent event) {
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
