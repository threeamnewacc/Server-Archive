package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class HelixTrail extends Particle {

    private double y = 2.5;
    private boolean down = false;
    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0.0D, 1, 0.0D);

    public HelixTrail() {
        super("helix_trail", ItemRarity.RARE, ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Helix Trail",
                ChatColor.GRAY + "Two strings of fire that circle", ChatColor.GRAY + "their owner for eternity."));
        this.speed = 2;
    }

    @Override
    public void spawnParticle(Player player) {
        Location loc = player.getLocation();

        double radius = 0.7;
        double x = radius * Math.cos((this.down ? -3 : 3) * this.y);
        double z = radius * Math.sin((this.down ? -3 : 3) * this.y);

        double y2 = 2.5 - this.y;

        Location loc2 = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y2, loc.getZ() + z);
        Location loc3 = new Location(loc.getWorld(), loc.getX() - x, loc.getY() + y2, loc.getZ() - z);

        this.particleLibrary.sendToLocation(player, loc2);
        this.particleLibrary.sendToLocation(player, loc3);

        if (this.y >= 2.5) {
            this.down = true;
        } else if (this.y <= 0.0) {
            this.down = false;
        }
        if (this.down) {
            this.y -= 0.15;
        } else {
            this.y += 0.15;
        }
    }
}
