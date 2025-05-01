package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SparkArrow extends ArrowTrail {

    public SparkArrow() {
        super("firework_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Firework Arrow Trail",
                ChatColor.GRAY + "'Coz baby you're a firework!"));

        this.particleType = ParticleLibrary.ParticleType.FIREWORKS_SPARK;
    }
}
