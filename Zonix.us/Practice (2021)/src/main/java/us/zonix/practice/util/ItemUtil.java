package us.zonix.practice.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class ItemUtil {
    private ItemUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }

    public static String formatMaterial(Material material) {
        String name = material.toString();
        name = name.replace('_', ' ');
        String result = "" + name.charAt(0);

        for (int i = 1; i < name.length(); i++) {
            if (name.charAt(i - 1) == ' ') {
                result = result + name.charAt(i);
            } else {
                result = result + Character.toLowerCase(name.charAt(i));
            }
        }

        return result;
    }

    public static ItemStack enchantItem(ItemStack itemStack, ItemUtil.ItemEnchant... enchantments) {
        Arrays.asList(enchantments).forEach(enchantment -> itemStack.addUnsafeEnchantment(enchantment.enchantment, enchantment.level));
        return itemStack;
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, int amount) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String name, int amount, short damage) {
        ItemStack item = new ItemStack(material, amount, damage);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack hideEnchants(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE});
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setUnbreakable(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_UNBREAKABLE});
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack renameItem(ItemStack item, String name) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack reloreItem(ItemStack item, String... lores) {
        return reloreItem(ItemUtil.ReloreType.OVERWRITE, item, lores);
    }

    public static ItemStack reloreItem(ItemUtil.ReloreType type, ItemStack item, String... lores) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new LinkedList<>();
        }

        switch (type) {
            case APPEND:
                lore.addAll(Arrays.asList(lores));
                meta.setLore(lore);
                break;
            case PREPEND:
                List<String> nLore = new LinkedList<>(Arrays.asList(lores));
                nLore.addAll(lore);
                meta.setLore(nLore);
                break;
            case OVERWRITE:
                meta.setLore(Arrays.asList(lores));
        }

        item.setItemMeta(meta);
        return item;
    }

    public static void removeItems(Inventory inventory, ItemStack item, int amount) {
        int size = inventory.getSize();

        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is != null && item.getType() == is.getType() && item.getDurability() == is.getDurability()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                } else {
                    inventory.setItem(slot, new ItemStack(Material.AIR));
                    amount = -newAmount;
                    if (amount == 0) {
                        break;
                    }
                }
            }
        }
    }

    public static ItemStack addItemFlag(ItemStack item, ItemFlag flag) {
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(new ItemFlag[]{flag});
        item.setItemMeta(meta);
        return item;
    }

    public static class ItemEnchant {
        private final Enchantment enchantment;
        private final int level;

        public ItemEnchant(Enchantment enchantment, int level) {
            this.enchantment = enchantment;
            this.level = level;
        }
    }

    public static enum ReloreType {
        OVERWRITE,
        PREPEND,
        APPEND;
    }
}
