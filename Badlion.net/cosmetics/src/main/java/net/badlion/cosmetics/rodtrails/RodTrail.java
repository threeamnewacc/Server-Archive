package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.EntityTrail;
import org.bukkit.inventory.ItemStack;

public abstract class RodTrail extends EntityTrail {

    public RodTrail(String name, ItemRarity rarity, ItemStack itemStack) {
        super(name, Cosmetics.CosmeticType.ROD_TRAIL, rarity, itemStack);
        this.speed = 2;
    }

}
