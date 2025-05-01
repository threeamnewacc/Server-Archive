package net.badlion.cosmetics.particles;

import net.badlion.cosmetics.utils.ParticleLibrary;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class CageTrail extends Particle {

    private int step = 0;
    private ParticleLibrary particleLibrary = new ParticleLibrary(ParticleLibrary.ParticleType.FLAME, 0, 10, 0);

    public CageTrail() {
        super("cage_trail", ItemRarity.UNCOMMON, ItemStackUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.GREEN + "Cage Trail", ChatColor.GRAY + "Live in your own personal fiery cage!"));
    }

    @Override
    public void spawnParticle(Player player) {
        List<Location> locations = new ArrayList<>();
        for (int stage = 0; stage < 12; stage++) {
            int x, z, x2, z2;
            int y2 = 0;
            int y = 0;

            if (stage == 0) {
                x = -1;
                z = 1;
                x2 = 1;
                z2 = 1;
            } else if (stage == 1) {
                x = 1;
                z = 1;
                x2 = 1;
                z2 = -1;
            } else if (stage == 2) {
                x = 1;
                z = -1;
                x2 = -1;
                z2 = -1;
            } else if (stage == 3) {
                x = -1;
                z = -1;
                x2 = -1;
                z2 = 1;
            } else if (stage == 4) {
                x = -1;
                y = 2;
                z = 1;
                x2 = 1;
                y2 = 2;
                z2 = 1;
            } else if (stage == 5) {
                x = 1;
                y = 2;
                z = 1;
                x2 = 1;
                y2 = 2;
                z2 = -1;
            } else if (stage == 6) {
                x = 1;
                y = 2;
                z = -1;
                x2 = -1;
                y2 = 2;
                z2 = -1;
            } else if (stage == 7) {
                x = -1;
                y = 2;
                z = -1;
                x2 = -1;
                y2 = 2;
                z2 = 1;
            } else if (stage == 8) {
                x = -1;
                y = 2;
                z = -1;
                x2 = -1;
                y2 = 0;
                z2 = -1;
            } else if (stage == 9) {
                x = 1;
                y = 2;
                z = 1;
                x2 = 1;
                y2 = 0;
                z2 = 1;
            } else if (stage == 10) {
                x = 1;
                y = 2;
                z = -1;
                x2 = 1;
                y2 = 0;
                z2 = -1;
            } else if (stage == 11) {
                x = -1;
                y = 2;
                z = 1;
                x2 = -1;
                y2 = 0;
                z2 = 1;
            } else {
                break;
            }

            Location location = player.getLocation().add(x, y + 0.2D, z);
            Location target = player.getLocation().add(x2, y2 + 0.2D, z2);

            Vector link = target.toVector().subtract(location.toVector());
            float length = (float) link.length();
            link.normalize();

            float ratio = length / 10;
            Vector v = link.multiply(ratio);
            Location loc = location.clone().subtract(v);
            locations.add(loc.add(v));
        }

        this.particleLibrary.sendToLocation(player, locations);
    }
}
