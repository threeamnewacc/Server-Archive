package net.badlion.potpvp.kits.soup;

import net.badlion.potpvp.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DefaultKit extends Kit {

    public DefaultKit(ItemStack item) {
        super(item);

        this.armorItems[3] = new ItemStack(Material.IRON_HELMET);
        this.armorItems[2] = new ItemStack(Material.IRON_CHESTPLATE);
        this.armorItems[1] = new ItemStack(Material.IRON_LEGGINGS);
        this.armorItems[0] = new ItemStack(Material.IRON_BOOTS);

        this.inventoryItems[0] = new ItemStack(Material.DIAMOND_SWORD);
        for (int i = 1; i < 36; i++) {
            this.inventoryItems[i] = new ItemStack(Material.MUSHROOM_SOUP);
        }
    }

}
