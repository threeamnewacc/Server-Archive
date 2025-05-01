package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class SlimeRod extends RodTrail {

    public SlimeRod() {
        super("slime_rod_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.SLIME_BALL, ChatColor.GREEN + "Slime Rod Trail",
                ChatColor.GRAY + "Such slime, much wow..."));

        this.particleType = ParticleLibrary.ParticleType.SLIME;

        this.amount = 5;
    }
}
