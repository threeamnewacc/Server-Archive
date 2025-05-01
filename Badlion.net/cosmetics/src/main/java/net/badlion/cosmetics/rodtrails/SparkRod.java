package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SparkRod extends RodTrail {

    public SparkRod() {
        super("firework_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Firework Rod Trail",
                ChatColor.GRAY + "'Coz baby you're a firework!"));

        this.particleType = ParticleLibrary.ParticleType.FIREWORKS_SPARK;
    }
}
