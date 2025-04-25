package us.zonix.hcfactions.factions.claims;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.FactionsPlugin;

@Getter
public class ClaimPillar {

    private static FactionsPlugin main = FactionsPlugin.getInstance();

    private Player player;
    private final Location originalLocation;
    private Location location;

    public ClaimPillar(Player player, Location location) {
        this.location = location;
        this.originalLocation = location.clone();
        this.location.setY(0);
        this.player = player;
    }

    public ClaimPillar show(Material material, int data) {
        Location loc = new Location(originalLocation.getWorld(), originalLocation.getX(), 0, originalLocation.getZ());
        int pos = 0;

        for (int i = 0; i < loc.getWorld().getMaxHeight(); i++) {
            if (pos == 5) {
                if (loc.getBlock().isEmpty()) {
                    player.sendBlockChange(loc, material, (byte) data);
                }

                pos = 0;
            }
            else {
                if (loc.getBlock().isEmpty()) {
                    player.sendBlockChange(loc, Material.GLASS, (byte) 0);
                }
            }

            pos += 1;
            loc.add(0, 1, 0);
        }

        return this;
    }

    public ClaimPillar remove() {
        Location loc = new Location(originalLocation.getWorld(), originalLocation.getX(), 0, originalLocation.getZ());

        for (int i = 0; i < loc.getWorld().getMaxHeight(); i++) {
            if (loc.getBlock().isEmpty()) {
                player.sendBlockChange(loc, Material.AIR, (byte) 0);
            }

            loc.add(0, 1, 0);
        }

        return this;
    }

}
