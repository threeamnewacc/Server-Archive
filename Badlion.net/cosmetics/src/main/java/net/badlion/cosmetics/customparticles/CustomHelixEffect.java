package net.badlion.cosmetics.customparticles;

import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CustomHelixEffect extends Particle {

    private double y = 2.5;
    private ParticleLibrary particleLibrary;

    public CustomHelixEffect(ParticleLibrary.ParticleType particleType) {
        super("custom_helix_trail", ItemRarity.SPECIAL, ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Custom Helix Trail"));

        this.speed = 2;

        this.particleLibrary = new ParticleLibrary(particleType, 0.0D, 1, 0.0D);
    }

    @Override
    public void spawnParticle(Player player) {
        Location loc = player.getLocation();

        double radius = 0.7;
        double x = radius * Math.cos(3 * this.y);
        double z = radius * Math.sin(3 * this.y);

        double y2 = 2.5 - this.y;

        Location loc2 = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y2, loc.getZ() + z);
        Location loc3 = new Location(loc.getWorld(), loc.getX() - x, loc.getY() + y2, loc.getZ() - z);

        this.particleLibrary.sendToLocation(player, loc2);
        this.particleLibrary.sendToLocation(player, loc3);

        if (this.y <= 0) {
            this.y = 2.5;
        } else {
            this.y -= 0.15;
        }
    }
}
