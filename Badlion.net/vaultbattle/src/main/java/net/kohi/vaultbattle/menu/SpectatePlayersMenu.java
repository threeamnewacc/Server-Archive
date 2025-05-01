package net.kohi.vaultbattle.menu;

import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class SpectatePlayersMenu extends AbstractMenu {

    public SpectatePlayersMenu(VaultBattlePlugin plugin) {
        super(plugin, 54, "");
    }

    @Override
    public void open(Player player) {
        update();
        player.openInventory(inventory);
    }


    public void update() {
        inventory.clear();
        int index = 0;
        for (Team team : plugin.getTeamManager().getTeams()) {
            for (Player player : team.getOnlinePlayers()) {
                ItemStack skull = ItemStackUtil.createItem(
                        Material.SKULL_ITEM,
                        (short) 3,
                        team.getColor().toChatColor() + player.getName());
                inventory.setItem(index, skull);
                index++;
                if (index == inventory.getSize()) {
                    return;
                }
            }
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (inventory.equals(event.getClickedInventory())) {
            if (event.getSlot() >= 0 && event.getSlot() < inventory.getSize()) {
                ItemStack item = inventory.getItem(event.getSlot());
                if (item != null && item.getType() == Material.SKULL_ITEM) {
                    if (item.getItemMeta().hasDisplayName()) {
                        String name = item.getItemMeta().getDisplayName();
                        name = ChatColor.stripColor(name);
                        Player player = Bukkit.getPlayer(name);
                        if (player != null) {
                            event.getWhoClicked().teleport(player);
                        }
                    }
                }
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
