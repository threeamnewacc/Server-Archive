package net.badlion.cosmetics.customparticles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowman;

import java.util.ArrayList;
import java.util.List;

public class SnowmanEffect {

    private static ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.SNOWBALL, 0, 1, 0);

    public static void spawnParticle(Snowman snowman, Player player) {
        Location location1 = snowman.getLocation();
        double radius = 1.0;
        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            double angle = 2 * Math.PI * i / 25;
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            locations.add(location1.clone().add(x, 0.0D, z));
        }
        particleLibrary.sendToLocation(player, locations);
    }
}
