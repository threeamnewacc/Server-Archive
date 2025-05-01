package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class DiscoRod extends RodTrail {

    public DiscoRod() {
        super("disco_rod_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.WOOL, ChatColor.GREEN + "Disco Rod Trail",
                ChatColor.GRAY + "Disco, disco, whoop, whoop!"));

        this.particleType = ParticleLibrary.ParticleType.REDSTONE;

        this.particleLibrary = new ParticleLibrary(particleType, 1, amount, 0);
    }

}
