package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class PortalRod extends RodTrail {

    public PortalRod() {
        super("portal_rod_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.ENDER_PORTAL_FRAME, ChatColor.GREEN + "Portal Rod Trail",
                ChatColor.GRAY + "Travel to a different dimension", ChatColor.GRAY + "with the Portal arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.PORTAL;

        this.amount = 5;
    }
}
