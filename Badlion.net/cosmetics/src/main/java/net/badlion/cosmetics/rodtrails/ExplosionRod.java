package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ExplosionRod extends RodTrail {

    public ExplosionRod() {
        super("explosion_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.TNT, ChatColor.GREEN + "Explosion Rod Trail",
                ChatColor.GRAY + "Make em up, up, up!"));

        this.particleType = ParticleLibrary.ParticleType.EXPLOSION_NORMAL;
    }
}
