package net.badlion.potpvp.kits.soup;

import net.badlion.potpvp.kits.Kit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ChainmailKit extends Kit {

    public ChainmailKit(ItemStack item) {
        super(item);

        this.armorItems[3] = new ItemStack(Material.CHAINMAIL_HELMET);
        this.armorItems[2] = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
        this.armorItems[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
        this.armorItems[0] = new ItemStack(Material.CHAINMAIL_BOOTS);

        this.inventoryItems[0] = new ItemStack(Material.IRON_SWORD);
        this.inventoryItems[0].addEnchantment(Enchantment.DAMAGE_ALL, 2);
        for (int i = 1; i < 36; i++) {
            this.inventoryItems[i] = new ItemStack(Material.MUSHROOM_SOUP);
        }
    }

}
