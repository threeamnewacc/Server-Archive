package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class FootstepRod extends RodTrail {

    public FootstepRod() {
        super("footstep_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.APPLE, ChatColor.GREEN + "Footstep Rod Trail",
                ChatColor.GRAY + "Invisible feet create footsteps", ChatColor.GRAY + "in the air..."));

        this.particleType = ParticleLibrary.ParticleType.FOOTSTEP;
    }
}
