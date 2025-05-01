package net.badlion.potpvp.kits.soup;

import net.badlion.potpvp.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class GoldKit extends Kit {

    public GoldKit(ItemStack item) {
        super(item);

        this.armorItems[3] = new ItemStack(Material.GOLD_HELMET);
        this.armorItems[3].addEnchantment(Enchantment.DURABILITY, 3);
        this.armorItems[2] = new ItemStack(Material.GOLD_CHESTPLATE);
        this.armorItems[2].addEnchantment(Enchantment.DURABILITY, 3);
        this.armorItems[1] = new ItemStack(Material.GOLD_LEGGINGS);
        this.armorItems[1].addEnchantment(Enchantment.DURABILITY, 3);
        this.armorItems[0] = new ItemStack(Material.GOLD_BOOTS);
        this.armorItems[0].addEnchantment(Enchantment.DURABILITY, 3);

        this.inventoryItems[0] = new ItemStack(Material.GOLD_SWORD);
        this.inventoryItems[0].addEnchantment(Enchantment.DURABILITY, 3);
        for (int i = 1; i < 36; i++) {
            this.inventoryItems[i] = new ItemStack(Material.MUSHROOM_SOUP);
        }
    }

}
