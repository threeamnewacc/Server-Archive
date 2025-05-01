package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class GreenRod extends RodTrail {

    public GreenRod() {
        super("green_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Green Rod Trail",
                Collections.singletonList(ChatColor.GRAY + "I want green.")));

        this.particleType = ParticleLibrary.ParticleType.VILLAGER_HAPPY;
    }
}
