package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.managers.FlightGCheatManager;
import net.badlion.cosmetics.utils.ParticleLibrary;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class LandTrail {

    public static void land(Player player, final ParticleLibrary.ParticleType particleType) {
        Location location = player.getLocation().clone().add(player.getLocation().getDirection().normalize().multiply(0.2D));
        Location target = location.clone().add(location.getDirection().normalize().multiply(1));

        Vector link = target.toVector().subtract(location.toVector());
        float length = (float) link.length();
        link.normalize();

        float ratio = length / 5;
        Vector v = link.multiply(ratio);
        final Location loc = location.clone().subtract(v);

        ParticleLibrary particleLibrary = new ParticleLibrary(particleType, 0, 5, 0);

        List<Location> locations = new ArrayList<>();
        for (double x = -5; x <= 5; x++) {
            for (double z = -5; z <= 5; z++) {
                for (int i = 0; i < 3; i++) {
                    locations.add(loc.clone().add(loc.getDirection().normalize().add(new Vector(x, 0.0D, z))).add(0.0D, i, 0.0D));
                }
            }
        }

        particleLibrary.sendToLocation(player, locations);

        for (Player pl : player.getWorld().getPlayers()) {
            if (player.getLocation().distance(pl.getLocation()) <= 10) {
                FlightGCheatManager.addToMapping(pl, 20 * 3);
                pl.setVelocity(pl.getVelocity().add(new Vector(0.0D, 1.0D, 0.0D)));
            }
        }
    }

}
