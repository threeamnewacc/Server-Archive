package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class CritArrow extends ArrowTrail {

    public CritArrow() {
        super("crit_arrow_trail", ItemRarity.SUPER_COMMON, ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Crit Arrow Trail",
                ChatColor.GRAY + "Crits do more damage,", ChatColor.GRAY + "but not these crits!"));

        this.particleType = ParticleLibrary.ParticleType.CRIT;
    }
}
