package net.badlion.potpvp.kits.soup;

import net.badlion.potpvp.kits.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DiamondKit extends Kit {

    public DiamondKit(ItemStack item) {
        super(item);

        this.armorItems[3] = new ItemStack(Material.DIAMOND_HELMET);
        this.armorItems[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.armorItems[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        this.armorItems[0] = new ItemStack(Material.DIAMOND_BOOTS);

        this.inventoryItems[0] = new ItemStack(Material.IRON_SWORD);
        for (int i = 1; i < 36; i++) {
            this.inventoryItems[i] = new ItemStack(Material.MUSHROOM_SOUP);
        }
    }

}
