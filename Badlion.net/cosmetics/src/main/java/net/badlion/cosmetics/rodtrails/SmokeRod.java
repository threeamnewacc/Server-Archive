package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SmokeRod extends RodTrail {

    public SmokeRod() {
        super("smoke_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.FLINT_AND_STEEL, ChatColor.GREEN + "Smoke Rod Trail", ChatColor.GRAY + "Just a puff of smoke."));

        this.particleType = ParticleLibrary.ParticleType.SMOKE_NORMAL;
    }
}
