// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.finalutil;

import java.util.List;
import java.util.Arrays;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public final class ItemUtil
{
    private ItemUtil() {
        throw new RuntimeException("Cannot instantiate a utility class.");
    }
    
    public static ItemStack createItem(final Material material, final String name) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createItem(final Material material, final String name, final int amount) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack createItem(final Material material, final String name, final int amount, final short damage) {
        final ItemStack item = new ItemStack(material, amount, damage);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack renameItem(final ItemStack item, final String name) {
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
    
    public static ItemStack reloreItem(final ItemStack item, final String... lores) {
        final ItemMeta meta = item.getItemMeta();
        meta.setLore((List)Arrays.asList(lores));
        item.setItemMeta(meta);
        return item;
    }
}
