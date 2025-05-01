package net.badlion.capturetheflag.inventories;

import net.badlion.capturetheflag.CTF;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ServerSelectorInventory {

    public static Inventory CTFInventory;
	                                    // TODO: NOT NEEDED ANYMORE BECAUSE OF MATCHMAKING?
    public static void initialize() {
        ServerSelectorInventory.CTFInventory = CTF.getInstance().getServer().createInventory(null, 54, ChatColor.GOLD + "Capture The Flag Server Selector");
    }

    public static ItemStack createCTFInventoryItem(String name, long playerCount, String status) {
        ItemStack item = null;
        if (status.equals("waiting")) {
            item = new ItemStack(Material.WOOL, (int) playerCount, (short) 5);
        } else if (status.equals("voting")) {
            item = new ItemStack(Material.WOOL, (int) playerCount, (short) 10);
        } else {
            item = new ItemStack(Material.WOOL, (int) playerCount, (short) 14);
        }

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GREEN + "Players: " + playerCount + "/24");
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

}
