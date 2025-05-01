package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class RedstoneArrow extends ArrowTrail {

    public RedstoneArrow() {
        super("redstone_arrow_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.GREEN + "Redstone Arrow Trail",
                ChatColor.GRAY + "Only the best electricians can", ChatColor.GRAY + "harness the power of Redstone."));

        this.particleType = ParticleLibrary.ParticleType.REDSTONE;
    }
}
