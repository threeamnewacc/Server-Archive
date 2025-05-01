package net.kohi.vaultbattle.menu.admin;

import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.menu.AbstractMenu;
import net.kohi.vaultbattle.type.GameMap;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class GameMapEditor extends AbstractMenu {

    private final GameMap[] maps;

    public GameMapEditor(VaultBattlePlugin plugin) {
        super(plugin, 27, "Admin Menu");
        maps = plugin.getGameMapManager().getMaps().toArray(new GameMap[0]);
        for (int i = 0; i < maps.length; i++) {
            ItemStack item = ItemStackUtil.createItem(Material.STAINED_CLAY, ChatColor.YELLOW + maps[i].getMapName());
            inventory.setItem(i, item);
        }
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        event.setCancelled(true);
        if (inventory.equals(event.getClickedInventory())) {
            if (event.getSlot() < maps.length && event.getSlot() >= 0) {
                GameMap map = maps[event.getSlot()];
                plugin.getGameMapManager().getAdminsEditing().put(player.getUniqueId(), map);
                player.sendMessage(ChatColor.GREEN + "Now editing " + map.getMapName());
                player.closeInventory();
                player.sendMessage(ChatColor.GOLD + "Loading the world where you can setup the teams or paste in the map.");
                World world = plugin.getGameMapManager().loadWorld(map);
                player.teleport(new Location(world, 0, 90, 0));
                player.setFlying(true);
                player.setGameMode(GameMode.CREATIVE);
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
