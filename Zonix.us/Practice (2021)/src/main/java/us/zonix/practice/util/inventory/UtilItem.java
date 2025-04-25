package us.zonix.practice.util.inventory;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UtilItem {
    public static ItemStack createItem(Material m, int amount) {
        return new ItemStack(m, amount);
    }

    public static ItemStack createItem(Material m, int amount, short durability) {
        return new ItemStack(m, amount, durability);
    }

    public static ItemStack createItem(Material m, int amount, short durability, String name) {
        ItemStack itemStack = new ItemStack(m, amount, durability);
        if (name != null) {
            itemStack = name(itemStack, name);
        }

        return itemStack;
    }

    public static ItemStack createItem(Material m, int amount, short durability, String name, Enchantment enchantment, int level) {
        ItemStack itemStack = new ItemStack(m, amount, durability);
        if (name != null) {
            itemStack = name(itemStack, name);
        }

        return enchantItem(itemStack, enchantment, level);
    }

    public static ItemStack createItem(Material m, int amount, short durability, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(m, amount, durability);
        if (name != null) {
            itemStack = name(itemStack, name);
        }

        if (lore != null) {
            itemStack = lore(itemStack, lore);
        }

        return itemStack;
    }

    public static ItemStack skull(ItemStack itemStack, String playerName) {
        SkullMeta meta = (SkullMeta)itemStack.getItemMeta();
        meta.setOwner(playerName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack name(ItemStack itemStack, String itemName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack lore(ItemStack itemStack, List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack enchantItem(ItemStack itemStack, Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return itemStack;
    }

    public static ItemStack effectItem(ItemStack itemStack, PotionEffectType potionEffectType, int time, int amplifier) {
        PotionMeta potionMeta = (PotionMeta)itemStack.getItemMeta();
        potionMeta.addCustomEffect(new PotionEffect(potionEffectType, time * 20, amplifier), true);
        itemStack.setItemMeta(potionMeta);
        return itemStack;
    }
}
