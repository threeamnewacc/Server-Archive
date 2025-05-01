package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class HeartArrow extends ArrowTrail {

    public HeartArrow() {
        super("heart_arrow_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Heart Arrow Trail",
                Collections.singletonList(ChatColor.GRAY + "Love is in the... Arrow?")));

        this.particleType = ParticleLibrary.ParticleType.HEART;
    }
}
