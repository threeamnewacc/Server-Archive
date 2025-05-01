package net.badlion.smellyinventory;

import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.ItemStack;

public class SmellyInventoryListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void inventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        // Grab the smelly inventory
        SmellyInventory smellyInventory = SmellyInventory.getSmellyInventory(event);
        if (smellyInventory != null) {
            // Cancel out if they didn't click an item
            if (item == null || item.getType().equals(Material.AIR)) {
                if ((event.getAction().equals(InventoryAction.HOTBAR_SWAP) || event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)
                        || event.getAction().equals(InventoryAction.NOTHING)) && !event.getClick().equals(ClickType.MIDDLE)) {
                    if (event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.CHEST)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                return;
            }

            // Retard bug abuser check
            if (!SmellyInventory.allowChestInventoryActions() && !event.getClick().equals(ClickType.MIDDLE) &&
                    (event.getAction().equals(InventoryAction.HOTBAR_SWAP) || event.getAction().equals(InventoryAction.HOTBAR_MOVE_AND_READD)
                            || event.getAction().equals(InventoryAction.NOTHING))) {
                if (event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.CHEST)) {
                    event.setCancelled(true);
                    return;
                }
            } else if (event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.CHEST)) {
                if (player.getOpenInventory() == null) {
                    event.setCancelled(true);
                    player.updateInventory();
                    return;
                }
            }

            // Cancel event by default
            event.setCancelled(true);

            if (SmellyInventory.isBackInventoryItem(item)) {
                BukkitUtil.openInventory(player, smellyInventory.getParentInventory(event));
                return;
            } else if (SmellyInventory.isCloseInventoryItem(item)) {
                BukkitUtil.closeInventory(player);
                return;
            }

            // Handle the click event
            smellyInventory.handleInventoryClick(event);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void inventoryCloseEvent(InventoryCloseEvent event) {
        // Grab the smelly inventory
        SmellyInventory smellyInventory = SmellyInventory.getSmellyInventory(event);
        if (smellyInventory != null) {
            // Handle the close event
            smellyInventory.handleInventoryClose(event);
        }
    }

    @EventHandler
    public void inventoryDragEvent(InventoryDragEvent event) {
        // Grab the smelly inventory
        SmellyInventory smellyInventory = SmellyInventory.getSmellyInventory(event.getView().getTopInventory());
        if (smellyInventory != null) {
            int size = event.getView().getTopInventory().getSize();
            for (Integer slot : event.getRawSlots()) {
                if (slot < size) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}