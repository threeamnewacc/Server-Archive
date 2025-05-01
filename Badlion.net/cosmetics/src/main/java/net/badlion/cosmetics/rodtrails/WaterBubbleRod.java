package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class WaterBubbleRod extends RodTrail {

    public WaterBubbleRod() {
        super("water_bubble_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.WATER_BUCKET, ChatColor.GREEN + "Water Bubble Rod Trail",
                ChatColor.GRAY + "They will call you", ChatColor.GRAY + "Captain Bubbles."));

        this.particleType = ParticleLibrary.ParticleType.WATER_SPLASH;

        this.amount = 5;
    }
}
