package me.devkevin.landcore.utils.item;

import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemMaker {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    private Map<Enchantment, Integer> enchantments = new HashMap<>();


    public ItemMaker(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemMaker(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.itemMeta = itemStack.getItemMeta();
    }

    public ItemMaker setOwner(String name) {
        SkullMeta meta = (SkullMeta) itemMeta;
        meta.setOwner(name);
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemMaker setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    public ItemMaker setDisplayName(String name) {
        itemMeta.setDisplayName(CC.translate(name));
        return this;
    }

    public ItemMaker setDurability(int durability) {
        itemStack.setDurability((short) durability);
        return this;
    }

    public ItemMaker addLore(String lore) {
        Object object = itemMeta.getLore();
        if (object == null) object = new ArrayList<>();

        ((List) object).add(CC.translate(lore));
        itemMeta.setLore((List<String>) object);
        return this;
    }

    public ItemMaker addLore(List<String> lore) {
        itemMeta.setLore(CC.translate(lore));
        return this;
    }

    public ItemMaker addLore(String... lore) {
        List<String> strings = new ArrayList<>();
        for (String string : lore) {
            strings.add(CC.translate(string));
        }
        itemMeta.setLore(strings);
        return this;
    }

    public ItemMaker setEnchant(Enchantment enchantment, int level) {
        enchantments.put(enchantment, level);
        return this;
    }

    public ItemMaker setUnbreakable(boolean unbreakable) {
        itemMeta.spigot().setUnbreakable(unbreakable);
        return this;
    }

    public ItemMaker setColor(Color color) {
        if (itemStack.getType() != null && itemStack.getType().name().contains("LEATHER")) {
            LeatherArmorMeta armorMeta = (LeatherArmorMeta) itemMeta;
            armorMeta.setColor(color);
        }
        return this;
    }

    public ItemStack create() {
        if (itemMeta != null) {
            itemStack.setItemMeta(itemMeta);
        }

        for (Enchantment enchantment : enchantments.keySet()) {
            itemStack.addEnchantment(enchantment, enchantments.get(enchantment));
        }

        return itemStack;
    }
}
