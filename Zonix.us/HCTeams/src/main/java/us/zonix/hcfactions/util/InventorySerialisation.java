package us.zonix.hcfactions.util;

import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class InventorySerialisation {

    public static String itemStackArrayToJson(ItemStack[] items) throws IllegalStateException {
        return GsonFactory.getCompactGson().toJson(items);
    }

    public static ItemStack[] itemStackArrayFromJson(String data) throws IOException {
        return GsonFactory.getCompactGson().fromJson(data, new TypeToken<ItemStack[]>() {}.getType());
    }
    
    public static boolean isHelmet(ItemStack itemStack) {
        return itemStack.getType() == Material.IRON_HELMET || itemStack.getType() == Material.LEATHER_HELMET || itemStack.getType() == Material.DIAMOND_HELMET || itemStack.getType() == Material.CHAINMAIL_HELMET || itemStack.getType() == Material.GOLD_HELMET;
    }

    public static boolean isChestplate(ItemStack itemStack) {
        return itemStack.getType() == Material.IRON_CHESTPLATE || itemStack.getType() == Material.LEATHER_CHESTPLATE || itemStack.getType() == Material.DIAMOND_CHESTPLATE || itemStack.getType() == Material.CHAINMAIL_CHESTPLATE || itemStack.getType() == Material.GOLD_CHESTPLATE;
    }

    public static boolean isLeggings(ItemStack itemStack) {
        return itemStack.getType() == Material.IRON_LEGGINGS || itemStack.getType() == Material.LEATHER_LEGGINGS || itemStack.getType() == Material.DIAMOND_LEGGINGS || itemStack.getType() == Material.CHAINMAIL_LEGGINGS || itemStack.getType() == Material.GOLD_LEGGINGS;
    }

    public static boolean isBoots(ItemStack itemStack) {
        return itemStack.getType() == Material.IRON_BOOTS || itemStack.getType() == Material.LEATHER_BOOTS || itemStack.getType() == Material.DIAMOND_BOOTS || itemStack.getType() == Material.CHAINMAIL_BOOTS || itemStack.getType() == Material.GOLD_BOOTS;
    }
}
