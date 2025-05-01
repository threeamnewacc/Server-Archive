package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class LavaDripArrow extends ArrowTrail {

    public LavaDripArrow() {
        super("lava_drip_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.BLAZE_POWDER, ChatColor.GREEN + "Lava Drip Arrow Trail",
                ChatColor.GRAY + "The air is the roof.", ChatColor.GRAY + "The air above is lava."));

        this.particleType = ParticleLibrary.ParticleType.DRIP_LAVA;
    }
}
