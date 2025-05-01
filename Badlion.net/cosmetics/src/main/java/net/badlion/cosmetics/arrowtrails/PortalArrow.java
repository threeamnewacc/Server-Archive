package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class PortalArrow extends ArrowTrail {

    public PortalArrow() {
        super("portal_arrow_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.ENDER_PORTAL_FRAME, ChatColor.GREEN + "Portal Arrow Trail",
                ChatColor.GRAY + "Travel to a different dimension", ChatColor.GRAY + "with the Portal arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.PORTAL;

        this.amount = 5;
    }
}
