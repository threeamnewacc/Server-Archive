package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChristmasHelixTrail extends Particle {

    private double x1 = 0.0D;
    private double z1 = 0.0D;
    private ParticleLibrary redstone = new ParticleLibrary(ParticleLibrary.ParticleType.REDSTONE, 0.0D, 1, 0.0D);
    private ParticleLibrary fireworks = new ParticleLibrary(ParticleLibrary.ParticleType.FIREWORKS_SPARK, 0.0D, 1, 0.0D);

    public ChristmasHelixTrail() {
        super("christmas_helix_trail", ItemRarity.RARE, ItemStackUtil.createItem(Material.GLOWSTONE_DUST, ChatColor.GREEN + "Christmas Helix Trail", ChatColor.GRAY + "Cover yourself in a red and white christmas themed helix!"));
        this.speed = 1;

    }

    @Override
    public void spawnParticle(Player player) {
        Location loc = player.getLocation();
        List<Location> redstoneLocations = new ArrayList<>();
        List<Location> fireworkLocations = new ArrayList<>();
        for (double y = 4; y >= 0; y -= 0.2) {
            double radius = y / 3;

            double x = radius * Math.cos(3 * y + this.x1);
            double z = radius * Math.sin(3 * y + this.z1);

            double y2 = 4 - y;

            Location loc2 = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y2, loc.getZ() + z);
            Location loc3 = new Location(loc.getWorld(), loc.getX() - x, loc.getY() + y2, loc.getZ() - z);

            redstoneLocations.add(loc2);
            fireworkLocations.add(loc2.add(0.0D, -0.2D, 0.0D));
            redstoneLocations.add(loc3);
            fireworkLocations.add(loc3.add(0.0D, -0.2D, 0.0D));
        }

        this.redstone.sendToLocation(player, redstoneLocations);
        this.fireworks.sendToLocation(player, fireworkLocations);

        this.x1 += 0.2;
        this.z1 += 0.2;
    }
}
