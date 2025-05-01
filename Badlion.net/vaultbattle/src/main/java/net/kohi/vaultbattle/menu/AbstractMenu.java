package net.kohi.vaultbattle.menu;

import net.kohi.vaultbattle.VaultBattlePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class AbstractMenu implements InventoryHolder {

    protected final VaultBattlePlugin plugin;

    protected final Inventory inventory;

    public AbstractMenu(VaultBattlePlugin plugin, int size, String title) {
        this.plugin = plugin;
        if (title.length() > 32) {
            title = title.substring(0, 32);
        }
        this.inventory = plugin.getServer().createInventory(this, size, title);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }

    public abstract void onInventoryClick(InventoryClickEvent event);

    public abstract void onInventoryDrag(InventoryDragEvent event);

    public abstract void onInventoryClose(InventoryCloseEvent event);

    public VaultBattlePlugin getPlugin() {
        return this.plugin;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}
