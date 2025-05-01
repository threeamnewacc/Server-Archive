package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GreenRingTrail extends Particle {

    private double i = 0.0D;
    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.VILLAGER_HAPPY, 0.0D, 1, 0.0D);

    public GreenRingTrail() {
        super("green_ring_trail", ItemRarity.RARE, ItemStackUtil.createItem(Material.EMERALD, ChatColor.GREEN + "Green Ring Trail",
                Arrays.asList(ChatColor.GRAY + "This trail circles the player,", ChatColor.GRAY + "it has nowhere else to go...")));

        this.speed = 1;

    }

    @Override
    public void spawnParticle(Player player) {
        double radius = 1;
        double points = 40;

        double slice = 6.2832D / points;
        double slice2 = 6.2832D / (points * 2.5D);

        Location ploca = player.getLocation();

        double angle = slice * i;
        double angle2 = slice2 * i;
        double newY = ploca.getY() + 1.0D + radius * Math.cos(angle2);
        double newX = ploca.getX() + radius * Math.cos(angle);
        double newZ = ploca.getZ() + radius * Math.sin(angle);

        Location p = new Location(player.getWorld(), newX, newY, newZ);
        this.particleLibrary.sendToLocation(player, p);
        i++;
    }
}
