package net.kohi.vaultbattle.menu.admin;

import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.menu.AbstractMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class AdminMenu extends AbstractMenu {


    public AdminMenu(VaultBattlePlugin plugin) {
        super(plugin, 9, "Admin Menu");
        ItemStack item = ItemStackUtil.createItem(Material.NAME_TAG, "Enter Admin Edit Mode");
        inventory.setItem(0, item);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if (inventory.equals(event.getClickedInventory())) {
            switch (event.getSlot()) {
                case 0:
                    if (plugin.getGameMapManager().isEditing(player)) {
                        player.sendMessage(ChatColor.RED + "You are already in edit mode.");
                        return;
                    } else {
                        new GameMapEditor(plugin).open(player);
                        player.sendMessage(ChatColor.GREEN + "Click a map to edit it.");
                    }
                    break;
            }
        }
    }

    @Override
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onInventoryClose(InventoryCloseEvent event) {
    }
}
