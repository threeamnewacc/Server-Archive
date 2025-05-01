package net.kohi.vaultbattle.listener;

import net.kohi.vaultbattle.menu.AbstractMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractMenu) {
            ((AbstractMenu) event.getInventory().getHolder()).onInventoryClick(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractMenu) {
            ((AbstractMenu) event.getInventory().getHolder()).onInventoryDrag(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof AbstractMenu) {
            ((AbstractMenu) event.getInventory().getHolder()).onInventoryClose(event);
        }
    }
}
