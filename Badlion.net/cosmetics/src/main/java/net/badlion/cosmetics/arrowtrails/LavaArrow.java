package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class LavaArrow extends ArrowTrail {

    public LavaArrow() {
        super("lava_arrow_trail", ItemRarity.COMMON, ItemStackUtil.createItem(Material.LAVA_BUCKET, ChatColor.GREEN + "Lava Arrow Trail",
                ChatColor.GRAY + "This will make you...", ChatColor.GRAY + "Hotter than the sun."));

        this.particleType = ParticleLibrary.ParticleType.LAVA;
    }
}
