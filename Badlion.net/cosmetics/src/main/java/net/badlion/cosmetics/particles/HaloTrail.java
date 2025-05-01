package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HaloTrail extends Particle {

    private ParticleLibrary particle = new ParticleLibrary(ParticleLibrary.ParticleType.FIREWORKS_SPARK, 0, 1, 0);

    public HaloTrail() {
        super("halo_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.FEATHER, ChatColor.GREEN + "Halo Trail", ChatColor.GRAY + "This trail will make you into an angel"));
    }

    @Override
    public void spawnParticle(Player player) {
        Location location1 = player.getEyeLocation().add(0.0D, 0.25D, 0.0D);
        int particles = 15;
        float radius = 0.4f;
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < particles; i++) {
            double angle = 2 * Math.PI * i / particles;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            locations.add(location1.clone().add(x, 0.3, z));
        }
        this.particle.sendToLocation(player, locations);
    }

}
