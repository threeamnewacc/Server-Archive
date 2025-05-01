package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SlimeArrow extends ArrowTrail {

    public SlimeArrow() {
        super("slime_arrow_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.SLIME_BALL, ChatColor.GREEN + "Slime Arrow Trail",
                ChatColor.GRAY + "Such slime, much wow..."));

        this.particleType = ParticleLibrary.ParticleType.SLIME;

        this.amount = 5;
    }
}
