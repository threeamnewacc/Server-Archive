package net.badlion.cosmetics.rodtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MusicRod extends RodTrail {

    public MusicRod() {
        super("music_rod_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.JUKEBOX, ChatColor.GREEN + "Music Rod Trail",
                ChatColor.GRAY + "Show your love for music", ChatColor.GRAY + "with the Music arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.NOTE;

        this.particleLibrary = new ParticleLibrary(particleType, 1, 1, 0);
    }

}
