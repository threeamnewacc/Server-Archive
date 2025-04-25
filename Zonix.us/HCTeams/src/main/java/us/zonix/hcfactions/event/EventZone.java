package us.zonix.hcfactions.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class EventZone {

    @Getter @Setter private final Location firstLocation, secondLocation;
    @Getter @Setter private final Location minPos, maxPos;
    @Getter @Setter private int height;

    public EventZone(Location firstLocation, Location secondLocation) {
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;
        this.minPos = this.getMinimum(this.firstLocation, this.secondLocation);
        this.maxPos = this.getMaximum(this.firstLocation, this.secondLocation);
    }

    private Location getMinimum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), (loc1.getX() < loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() < loc2.getY()) ? loc1.getY() : loc2.getY(), (loc1.getZ() < loc2.getZ()) ? loc1.getZ() : loc2.getZ());
    }

    private Location getMaximum(Location loc1, Location loc2) {
        return new Location(loc1.getWorld(), (loc1.getX() > loc2.getX()) ? loc1.getX() : loc2.getX(), (loc1.getY() > loc2.getY()) ? loc1.getY() + height : loc2.getY() + height, (loc1.getZ() > loc2.getZ()) ? loc1.getZ() : loc2.getZ());
    }


    private boolean isInAABB(Location pos, Location pos2, Location pos3) {
        Location min = getMinimum(pos2, pos3);
        Location max = getMaximum(pos2, pos3);
        if (min.getBlockX() <= pos.getBlockX() && max.getBlockX() >= pos.getBlockX() && min.getBlockY() <= pos.getBlockY() && max.getBlockY() >= pos.getBlockY() && min.getBlockZ() <= pos.getBlockZ() && max.getBlockZ() >= pos.getBlockZ()) {
            return true;
        }
        return false;
    }

    public boolean isInside(Player player) {
        if (player.getWorld() == this.firstLocation.getWorld()) {
            Location loc = player.getLocation();
            if (isInAABB(loc, minPos, maxPos)) {
                return true;
            }
        }
        return false;
    }

    public Location getCenter() {

        int x = (maxPos.getBlockX() + minPos.getBlockX()) / 2;
        int y = (maxPos.getBlockY() + minPos.getBlockY()) / 2;
        int z = (maxPos.getBlockZ() + minPos.getBlockZ()) / 2;

        return new Location(minPos.getWorld(), x, y, z);
    }

}
