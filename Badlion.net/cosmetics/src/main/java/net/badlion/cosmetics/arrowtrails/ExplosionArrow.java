package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ExplosionArrow extends ArrowTrail {

    public ExplosionArrow() {
        super("explosion_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.TNT, ChatColor.GREEN + "Explosion Arrow Trail",
                ChatColor.GRAY + "Make em up, up, up!"));

        this.particleType = ParticleLibrary.ParticleType.EXPLOSION_NORMAL;
    }
}
