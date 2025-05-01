package net.kohi.vaultbattle.menu;

import net.badlion.gberry.utils.ItemStackUtil;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.manager.GameManager;
import net.kohi.vaultbattle.type.Region;
import net.kohi.vaultbattle.type.Team;
import net.kohi.vaultbattle.type.TeamColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class SpectateWarpsMenu extends AbstractMenu {

    public SpectateWarpsMenu(VaultBattlePlugin plugin) {
        super(plugin, 54, "");
    }

    @Override
    public void open(Player player) {
        update();
        player.openInventory(inventory);
    }


    public void update() {
        inventory.clear();

        ItemStack redItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 1, ChatColor.RED + "Teleport to Red Team");
        ItemStack blueItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 4, ChatColor.BLUE + "Teleport to Blue Team");
        ItemStack yellowItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 11, ChatColor.YELLOW + "Teleport to Yellow Team");
        ItemStack greenItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 10, ChatColor.GREEN + "Teleport to Green Team");

        inventory.setItem(0, redItem);
        inventory.setItem(1, blueItem);
        inventory.setItem(2, yellowItem);
        inventory.setItem(3, greenItem);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (inventory.equals(event.getClickedInventory())) {
            if (event.getSlot() >= 0 && event.getSlot() < inventory.getSize()) {
                Player player = (Player) event.getWhoClicked();
                switch (event.getSlot()) {
                    case 0:
                        teleportBase(player, TeamColor.RED);
                        break;
                    case 1:
                        teleportBase(player, TeamColor.BLUE);
                        break;
                    case 2:
                        teleportBase(player, TeamColor.YELLOW);
                        break;
                    case 3:
                        teleportBase(player, TeamColor.GREEN);
                        break;
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

    private void teleportBase(Player player, TeamColor color) {
        Region bank = plugin.getGameManager().getMap().getBanks().get(color);
        Location loc = new Location(
                GameManager.gameMapWorld,
                (bank.getMin().getX() + bank.getMax().getX()) / 2,
                bank.getMin().getY(),
                (bank.getMin().getZ() + bank.getMax().getZ()) / 2);
        while (loc.getBlock().getType() != Material.AIR && loc.getY() < 255) {
            loc.add(0, 2, 0);
        }
        loc.add(0, 2, 0);
        player.teleport(loc);
        player.closeInventory();
    }
}
