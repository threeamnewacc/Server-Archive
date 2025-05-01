package net.badlion.skywarslobby.inventories;

import net.badlion.gberry.Gberry;
import net.badlion.skywarslobby.listeners.LobbyListener;
import net.badlion.skywarslobby.tasks.CheckQueueStatusTask;
import net.badlion.smellyinventory.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GameQueueInventory {

    private static SmellyInventory smellyInventory;

    public static void initialize() {
        GameQueueInventory.smellyInventory = new SmellyInventory(new GameQueueInventoryScreenHandler(), 18,
                                                                   ChatColor.BOLD + ChatColor.AQUA.toString() + "SkyWars Game Queues");

        ItemStack ffaClassic = ItemStackUtil.createItem(Material.EYE_OF_ENDER, ChatColor.AQUA + "SkyWars Classic FFA");
        GameQueueInventory.updateFFAClassicQueueCountInternal(ffaClassic);

        ItemStack ffaOP = ItemStackUtil.createItem(Material.EXP_BOTTLE, ChatColor.AQUA + "SkyWars OP FFA");
        GameQueueInventory.updateFFAClassicQueueCountInternal(ffaOP);

        GameQueueInventory.smellyInventory.getMainInventory().addItem(ffaClassic);
        GameQueueInventory.smellyInventory.getMainInventory().addItem(ffaOP);
    }

    public static void updateFFAClassicQueueCount() {
        ItemStack ffaClassic = GameQueueInventory.smellyInventory.getMainInventory().getItem(0);
        GameQueueInventory.updateFFAClassicQueueCountInternal(ffaClassic);
    }

    public static void updateFFAOpQueueCount() {
        ItemStack ffaOP = GameQueueInventory.smellyInventory.getMainInventory().getItem(1);
        GameQueueInventory.updateFFAOPQueueCountInternal(ffaOP);
    }

    private static void updateFFAClassicQueueCountInternal(ItemStack ffaClassic) {
        ItemMeta ffaClassicMeta = ffaClassic.getItemMeta();
        ffaClassicMeta.setDisplayName(ChatColor.YELLOW + "Normal FFA Queue");
        List<String> ffaClassicLore = new ArrayList<>();
        ffaClassicLore.add(ChatColor.DARK_GREEN + "" + CheckQueueStatusTask.ffaClassic + " in queue");
        ffaClassicLore.add("");
        ffaClassicLore.add(ChatColor.GOLD + "Click to join");
        ffaClassicMeta.setLore(ffaClassicLore);
        ffaClassic.setItemMeta(ffaClassicMeta);
    }

    private static void updateFFAOPQueueCountInternal(ItemStack ffaOP) {
        ItemMeta ffaOPMeta = ffaOP.getItemMeta();
        ffaOPMeta.setDisplayName(ChatColor.YELLOW + "OP FFA Queue");
        List<String> ffaClassicLore = new ArrayList<>();
        ffaClassicLore.add(ChatColor.DARK_GREEN + "" + CheckQueueStatusTask.ffaOp + " in queue");
        ffaClassicLore.add("");
        ffaClassicLore.add(ChatColor.GOLD + "Click to join");
        ffaOPMeta.setLore(ffaClassicLore);
        ffaOP.setItemMeta(ffaOPMeta);
    }

    public static void openGameQueueInventory(Player player) {
        BukkitUtil.openInventory(player, GameQueueInventory.smellyInventory.getMainInventory());
    }

    private static class GameQueueInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

        @Override
        public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
            switch (slot) {
	            case 0:
                    LobbyListener.joinFFAClassicQueue(player);
		            break;
                case 1:
                    LobbyListener.joinFFAOPQueue(player);
                    break;
            }

            BukkitUtil.closeInventory(player);
        }

        @Override
        public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

        }

    }

}
