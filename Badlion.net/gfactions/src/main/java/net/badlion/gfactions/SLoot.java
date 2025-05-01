package net.badlion.gfactions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SLoot {


    private static Map<Item, Player> protectedItemsMap = new HashMap<>();

    public static void dropLoot(Location dropPoint, List<ItemStack> itemsBeingDrop, List<Player> playersWithAccess, Integer protectionTime, Boolean protectLoot){

        List<Item> itemList = new ArrayList<>();
        for (ItemStack itemStack : itemsBeingDrop) {
            Item item = Bukkit.getWorld("world").dropItemNaturally(dropPoint, itemStack);
            itemList.add(item);
        }
        if (protectLoot) {
            for (Player player : playersWithAccess) {
                SLoot.protectLoot(itemList, player, protectionTime);
            }
        }
    }

    public static void protectLoot(final List<Item> protectedItems, Player player, Integer protectionTime) {
        for (Item item : protectedItems) {
            SLoot.protectedItemsMap.put(item, player);
        }

        GFactions.plugin.getServer().getScheduler().runTaskLater(GFactions.plugin, new Runnable() {
            @Override
            public void run() {
                for (Item item : protectedItems) {
                    SLoot.protectedItemsMap.remove(item);
                }
            }
        }, protectionTime * 20);
    }

    public static boolean checkTheirPrivilege(Player looter, Item loot){
        return SLoot.protectedItemsMap.get(loot) == null || SLoot.protectedItemsMap.get(loot) == looter;
    }

}
