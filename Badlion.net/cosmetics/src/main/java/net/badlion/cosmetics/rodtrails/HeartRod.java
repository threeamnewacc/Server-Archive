package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class HeartRod extends RodTrail {

    public HeartRod() {
        super("heart_rod_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Heart Rod Trail",
                Collections.singletonList(ChatColor.GRAY + "Love is in the... Rod?")));

        this.particleType = ParticleLibrary.ParticleType.HEART;
    }
}
