package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class GreenArrow extends ArrowTrail {

    public GreenArrow() {
        super("green_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Green Arrow Trail",
                Collections.singletonList(ChatColor.GRAY + "I want green.")));

        this.particleType = ParticleLibrary.ParticleType.VILLAGER_HAPPY;
    }
}
