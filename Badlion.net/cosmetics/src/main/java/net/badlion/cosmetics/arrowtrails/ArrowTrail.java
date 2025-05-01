package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.EntityTrail;
import org.bukkit.inventory.ItemStack;

public abstract class ArrowTrail extends EntityTrail {

    public ArrowTrail(String name, ItemRarity rarity, ItemStack itemStack) {
        super(name, Cosmetics.CosmeticType.ARROW_TRAIL, rarity, itemStack);
    }

}
