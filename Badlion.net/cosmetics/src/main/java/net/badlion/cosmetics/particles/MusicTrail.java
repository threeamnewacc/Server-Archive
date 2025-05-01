package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MusicTrail extends Particle {

    private List<Float> dMajorScale = new ArrayList<>();
    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.NOTE, 0, 1, 0);

    public MusicTrail() {
        super("music_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.JUKEBOX, ChatColor.GREEN + "Music Trail", ChatColor.GRAY + "Music to my ears!"));

        this.dMajorScale.add(0.8F);
        this.dMajorScale.add(0.9F);
        this.dMajorScale.add(1.0F);
        this.dMajorScale.add(1.05F);
        this.dMajorScale.add(1.2F);
        this.dMajorScale.add(1.35F);
        this.dMajorScale.add(1.55F);
        this.dMajorScale.add(1.6F);
    }

    @Override
    public void spawnParticle(Player player) {
        particle.sendToLocation(player, player.getLocation(), true, 0.3D, 5);

        // Play an actual sound
        //float volume = (float) (0.25d + Math.random() - Math.random());
        //float pitch = (float) (1D + Math.random() - Math.random());

        //player.playSound(player.getLocation(), Sound.NOTE_PIANO, volume,
        //		this.dMajorScale.get(Gberry.generateRandomInt(0, this.dMajorScale.size() - 1)));
    }

}
