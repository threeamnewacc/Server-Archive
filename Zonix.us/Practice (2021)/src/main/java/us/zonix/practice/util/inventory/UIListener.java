package us.zonix.practice.util.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class UIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getInventory() != null) {
            if (event.getInventory().getHolder() instanceof InventoryUI.InventoryUIHolder) {
                if (event.getCurrentItem() != null) {
                    InventoryUI.InventoryUIHolder inventoryUIHolder = (InventoryUI.InventoryUIHolder)event.getInventory().getHolder();
                    event.setCancelled(true);
                    if (event.getClickedInventory() != null && event.getInventory().equals(event.getClickedInventory())) {
                        InventoryUI ui = inventoryUIHolder.getInventoryUI();
                        InventoryUI.ClickableItem item = ui.getCurrentUI().getItem(event.getSlot());
                        if (item != null) {
                            item.onClick(event);
                        }
                    }
                }
            }
        }
    }
}
