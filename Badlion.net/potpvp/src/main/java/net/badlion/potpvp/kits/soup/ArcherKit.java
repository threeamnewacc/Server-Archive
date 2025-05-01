package net.badlion.potpvp.kits.soup;

import net.badlion.potpvp.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ArcherKit extends Kit {

    public ArcherKit(ItemStack item) {
        super(item);

        this.armorItems[3] = new ItemStack(Material.LEATHER_HELMET);
        this.armorItems[3].addEnchantment(Enchantment.DURABILITY, 3);
        this.armorItems[2] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        this.armorItems[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        this.armorItems[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        this.armorItems[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        this.armorItems[1].addEnchantment(Enchantment.DURABILITY, 3);
        this.armorItems[0] = new ItemStack(Material.DIAMOND_BOOTS);

        this.inventoryItems[1] = new ItemStack(Material.BOW);
        this.inventoryItems[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);
        this.inventoryItems[1].addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        this.inventoryItems[0] = new ItemStack(Material.WOOD_SWORD);
        this.inventoryItems[0].addEnchantment(Enchantment.DURABILITY, 3);
        this.inventoryItems[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
        this.inventoryItems[9] = new ItemStack(Material.ARROW);
        for (int i = 2; i < 36; i++) {
            if (i == 9) continue;
            this.inventoryItems[i] = new ItemStack(Material.MUSHROOM_SOUP);
        }
    }

}
