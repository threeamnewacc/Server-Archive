package net.badlion.cosmetics.arrowtrails;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class MusicArrow extends ArrowTrail {

    public MusicArrow() {
        super("music_arrow_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.JUKEBOX, ChatColor.GREEN + "Music Arrow Trail",
                ChatColor.GRAY + "Show your love for music", ChatColor.GRAY + "with the Music arrow trail!"));

        this.particleType = ParticleLibrary.ParticleType.NOTE;

        this.particleLibrary = new ParticleLibrary(particleType, 1, 1, 0);
    }

}
