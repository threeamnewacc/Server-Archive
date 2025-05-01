package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class FlameRingTrail extends Particle {

    private final double radius = 1;
    private final ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0.0D, 1, 0.0D);
    private double i = 0;

    public FlameRingTrail() {
        super("flame_ring_trail", ItemRarity.SUPER_RARE, ItemStackUtil.createItem(Material.MAGMA_CREAM, ChatColor.GREEN + "Flame Ring Trail",
                Arrays.asList(ChatColor.GRAY + "Some say that this ring was forged", ChatColor.GRAY + "from the bottom of the nether...")));
        this.speed = 1;

    }

    @Override
    public void spawnParticle(final Player player) {
        World world = player.getWorld();
        Location location = player.getLocation();

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        double slice = 6.283185307179586D / 40;
        double angle = slice * this.i;
        double angle2 = -slice * this.i;

        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);
        double cosA2 = Math.cos(angle2);
        double sinA2 = Math.sin(angle2);

        double newX = x - this.radius * cosA;
        double newZ = z - this.radius * sinA;
        double newY2 = y + 1.0D + this.radius * cosA;
        double newX2 = x + this.radius * cosA2;
        double newZ2 = z + this.radius * sinA2;

        Location loc1 = new Location(world, newX, newY2, newZ);
        Location loc2 = new Location(world, newX2, newY2, newZ2);

        this.particleLibrary.sendToLocation(player, loc1);
        this.particleLibrary.sendToLocation(player, loc2);
        this.i += 1.0D;
    }
}
