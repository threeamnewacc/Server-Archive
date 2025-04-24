// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.listener;

import java.beans.ConstructorProperties;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.EventHandler;
import club.mineman.core.gui.GuiItem;
import java.util.Optional;
import club.mineman.core.gui.GuiClickable;
import club.mineman.core.gui.GuiFolder;
import org.bukkit.event.inventory.InventoryClickEvent;
import club.mineman.core.CorePlugin;
import org.bukkit.event.Listener;

public class GuiListener implements Listener
{
    private final CorePlugin plugin;
    
    @EventHandler
    void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() == null) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        final GuiFolder folder;
        final Optional<GuiFolder> found = this.plugin.getFolders().stream().filter(folder -> folder.getInventory().getName().equalsIgnoreCase(e.getInventory().getName())).findFirst();
        if (!found.isPresent()) {
            return;
        }
        folder = found.get();
        final GuiItem item = folder.getCurrentPage().getItem(e.getSlot());
        if (item == null) {
            return;
        }
        e.setCancelled(true);
        if (item instanceof GuiClickable) {
            ((GuiClickable)item).onClick(e);
        }
    }
    
    @EventHandler
    void onInventoryClose(final InventoryCloseEvent e) {
        if (e.getInventory() == null) {
            return;
        }
        final GuiFolder folder;
        final Optional<GuiFolder> found = this.plugin.getFolders().stream().filter(folder -> folder.getInventory().getName().equalsIgnoreCase(e.getInventory().getName())).findFirst();
        if (!found.isPresent()) {
            return;
        }
        folder = found.get();
        this.plugin.getFolders().remove(folder);
    }
    
    @ConstructorProperties({ "plugin" })
    public GuiListener(final CorePlugin plugin) {
        this.plugin = plugin;
    }
}
