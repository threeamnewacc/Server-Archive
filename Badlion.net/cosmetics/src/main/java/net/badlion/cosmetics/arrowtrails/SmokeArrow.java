package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SmokeArrow extends ArrowTrail {

    public SmokeArrow() {
        super("smoke_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.FLINT_AND_STEEL, ChatColor.GREEN + "Smoke Arrow Trail", ChatColor.GRAY + "Just a puff of smoke."));

        this.particleType = ParticleLibrary.ParticleType.SMOKE_NORMAL;
    }
}
