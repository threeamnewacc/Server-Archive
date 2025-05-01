package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class DiscoArrow extends ArrowTrail {

    public DiscoArrow() {
        super("disco_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.WOOL, ChatColor.GREEN + "Disco Arrow Trail",
                ChatColor.GRAY + "Disco, disco, whoop, whoop!"));

        this.particleType = ParticleLibrary.ParticleType.REDSTONE;

        this.particleLibrary = new ParticleLibrary(particleType, 1, amount, 0);
    }

}
