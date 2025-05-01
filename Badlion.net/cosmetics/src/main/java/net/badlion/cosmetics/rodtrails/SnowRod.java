package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SnowRod extends RodTrail {

    public SnowRod() {
        super("snow_rod_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.SNOW_BALL, ChatColor.GREEN + "Snow Rod Trail",
                ChatColor.GRAY + "Show your love for the snow", ChatColor.GRAY + "with the Snow arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.SNOW_SHOVEL;
    }
}
