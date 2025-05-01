package net.badlion.gcheat.listeners;

import net.badlion.gcheat.GCheat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventoryTweakListener implements Listener {

    private GCheat plugin;

    private Set<UUID> inventoriesOpened = new HashSet<>();

    public InventoryTweakListener(GCheat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDrinkSoup(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (event.getItem() != null && event.getItem().getType() == Material.MUSHROOM_SOUP) {
                Player player = event.getPlayer();
                if (this.inventoriesOpened.contains(player.getUniqueId())) {
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mc [Inventory Hacks] " + player.getName() + " is using inventory hacks");
                    this.plugin.logMessage(player, "[" + player.getUniqueId() + "] [" + player.getName() + "] is using inventory hacks");
                }
            }
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent event) {
        if (event.getInventory().getType().equals(InventoryType.CRAFTING)) { // Player inventory
            if (event.getRawSlot() > 8) {
                if (!this.inventoriesOpened.contains(event.getWhoClicked().getUniqueId())) {
                    this.inventoriesOpened.add(event.getWhoClicked().getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryCloseEvent event) {
        this.inventoriesOpened.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void playerQuitEvent(PlayerQuitEvent event) {
        // Safety check
        if (this.inventoriesOpened.contains(event.getPlayer().getUniqueId())) {
            this.inventoriesOpened.remove(event.getPlayer().getUniqueId());
        }
    }

}
