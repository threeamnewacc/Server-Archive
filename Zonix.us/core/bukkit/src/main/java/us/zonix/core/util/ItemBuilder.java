package us.zonix.core.util;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;
import us.zonix.core.rank.Rank;

import java.util.ArrayList;
import java.util.List;

/*
    not mine so fuck u all lol gang nigga shit #3hunna bang shang lang config gang shank nigers4free crip gang bloods every day u feel me DOGGG
 */
public class ItemBuilder implements Listener {

    private final ItemStack is;

    public ItemBuilder(final Material mat) {
        is = new ItemStack(mat);
    }

    public ItemBuilder(final ItemStack is) {
        this.is = is;
    }

    public ItemBuilder amount(final int amount) {
        is.setAmount(amount);
        return this;
    }

    public ItemBuilder name(final String name) {
        final ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final String name) {
        final ItemMeta meta = is.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(name);
        meta.setLore(lore);
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder lore(final List<String> lore) {
        List<String> toSet = new ArrayList<>();
        ItemMeta meta = is.getItemMeta();

        for (String string : lore) {
            toSet.add(ChatColor.translateAlternateColorCodes('&', string));
        }

        meta.setLore(toSet);
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder durability(final int durability) {
        is.setDurability((short) durability);
        return this;
    }

    @SuppressWarnings("deprecation")
    public ItemBuilder data(final int data) {
        is.setData(new MaterialData(is.getType(), (byte) data));
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment, final int level) {
        is.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemBuilder enchantment(final Enchantment enchantment) {
        is.addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    public ItemBuilder type(final Material material) {
        is.setType(material);
        return this;
    }

    public ItemBuilder clearLore() {
        final ItemMeta meta = is.getItemMeta();
        meta.setLore(new ArrayList<String>());
        is.setItemMeta(meta);
        return this;
    }

    public ItemBuilder clearEnchantments() {
        for (final Enchantment e : is.getEnchantments().keySet()) {
            is.removeEnchantment(e);
        }
        return this;
    }

    public ItemBuilder color(Color color) {
        if (is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET
                || is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
            meta.setColor(color);
            is.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemBuilder color(Rank rank) {

        Color color = Color.WHITE;

        if(rank == Rank.SILVER) {
            color = Color.SILVER;
        } else if(rank == Rank.GOLD) {
            color = Color.ORANGE;
        } else if(rank == Rank.PLATINUM) {
            color = Color.TEAL;
        } else if(rank == Rank.EMERALD) {
            color = Color.GREEN;
        } else if(rank == Rank.ZONIX) {
            color = Color.RED;
        } else if(rank == Rank.BUILDER) {
            color = Color.BLUE;
        } else if(rank == Rank.E_GIRL || rank == Rank.PARTNER || rank == Rank.MEDIA) {
            color = Color.FUCHSIA;
        } else if(rank == Rank.TRIAL_MOD || rank == Rank.MODERATOR || rank == Rank.SENIOR_MODERATOR) {
            color = Color.PURPLE;
        } else if(rank == Rank.ADMINISTRATOR || rank == Rank.MANAGER) {
            color = Color.RED;
        } else if(rank == Rank.DEVELOPER) {
            color = Color.AQUA;
        } else if(rank == Rank.OWNER || rank == Rank.MEDIA_OWNER) {
            color = Color.MAROON;
        }

        if (is.getType() == Material.LEATHER_BOOTS || is.getType() == Material.LEATHER_CHESTPLATE || is.getType() == Material.LEATHER_HELMET
                || is.getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) is.getItemMeta();
            meta.setColor(color);
            is.setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    public ItemStack build() {
        return is;
    }

}