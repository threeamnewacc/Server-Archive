package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.Collections;

public class FlameRod extends RodTrail {

    public FlameRod() {
        super("flame_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.FIRE, ChatColor.GREEN + "Flame Rod Trail",
                Collections.singletonList(ChatColor.GRAY + "Fire awayyy... Fire away!")));

        this.particleType = ParticleLibrary.ParticleType.FLAME;

        this.speed = 1;
    }
}
