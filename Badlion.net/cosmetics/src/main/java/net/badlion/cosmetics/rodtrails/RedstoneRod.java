package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class RedstoneRod extends RodTrail {

    public RedstoneRod() {
        super("redstone_rod_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.GREEN + "Redstone Rod Trail",
                ChatColor.GRAY + "Only the best electricians can", ChatColor.GRAY + "harness the power of Redstone."));

        this.particleType = ParticleLibrary.ParticleType.REDSTONE;
    }
}
