package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SnowArrow extends ArrowTrail {

    public SnowArrow() {
        super("snow_arrow_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.SNOW_BALL, ChatColor.GREEN + "Snow Arrow Trail",
                ChatColor.GRAY + "Show your love for the snow", ChatColor.GRAY + "with the Snow arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.SNOW_SHOVEL;
    }
}
