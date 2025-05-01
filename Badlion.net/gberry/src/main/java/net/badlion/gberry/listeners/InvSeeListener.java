package net.badlion.gberry.listeners;

import net.badlion.gberry.Gberry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InvSeeListener implements Listener {

    private Gberry plugin;

    public InvSeeListener(Gberry plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent e) {
        if (this.plugin.getInvSeeing().contains(e.getWhoClicked())) {
            if (!e.getWhoClicked().isOp()) {
                // Don't let non-ops edit
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e) {
        // Remove from out lists
        this.plugin.getInvSeeing().remove(e.getPlayer());
    }

}
