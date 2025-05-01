package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class LavaRod extends RodTrail {

    public LavaRod() {
        super("lava_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.LAVA_BUCKET, ChatColor.GREEN + "Lava Rod Trail",
                ChatColor.GRAY + "This will make you...", ChatColor.GRAY + "Hotter than the sun."));

        this.particleType = ParticleLibrary.ParticleType.LAVA;
    }
}
