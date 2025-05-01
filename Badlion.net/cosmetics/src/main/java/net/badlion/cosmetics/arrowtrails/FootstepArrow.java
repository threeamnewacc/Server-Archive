package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class FootstepArrow extends ArrowTrail {

    public FootstepArrow() {
        super("footstep_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.APPLE, ChatColor.GREEN + "Footstep Arrow Trail",
                ChatColor.GRAY + "Invisible feet create footsteps", ChatColor.GRAY + "in the air..."));

        this.particleType = ParticleLibrary.ParticleType.FOOTSTEP;
    }
}
