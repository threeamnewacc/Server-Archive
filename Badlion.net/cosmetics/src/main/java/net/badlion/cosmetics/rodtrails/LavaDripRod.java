package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class LavaDripRod extends RodTrail {

    public LavaDripRod() {
        super("lava_drip_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.BLAZE_POWDER, ChatColor.GREEN + "Lava Drip Rod Trail",
                ChatColor.GRAY + "The air is the roof.", ChatColor.GRAY + "The air above is lava."));

        this.particleType = ParticleLibrary.ParticleType.DRIP_LAVA;
    }
}
