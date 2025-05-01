package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class WaterBubbleArrow extends ArrowTrail {

    public WaterBubbleArrow() {
        super("water_bubble_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.WATER_BUCKET, ChatColor.GREEN + "Water Bubble Arrow Trail",
                ChatColor.GRAY + "They will call you", ChatColor.GRAY + "Captain Bubbles."));

        this.particleType = ParticleLibrary.ParticleType.WATER_SPLASH;

        this.amount = 5;
    }
}
