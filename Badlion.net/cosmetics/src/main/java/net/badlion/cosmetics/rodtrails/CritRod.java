package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CritRod extends RodTrail {

    public CritRod() {
        super("crit_rod_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Crit Rod Trail",
                ChatColor.GRAY + "Crits do more damage,", ChatColor.GRAY + "but not these crits!"));

        this.particleType = ParticleLibrary.ParticleType.CRIT;
    }
}
