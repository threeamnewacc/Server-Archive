package net.kohi.vaultbattle.type;

import net.kohi.vaultbattle.manager.GameManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Region {

    private final SimpleLocation pointA;
    private final SimpleLocation pointB;

    public Region(SimpleLocation pointA, SimpleLocation pointB) {
        this.pointA = pointA;
        this.pointB = pointB;
    }


    public Vector getMin() {
        double minx = Math.min(pointA.getX(), pointB.getX());
        double miny = Math.min(pointA.getY(), pointB.getY());
        double minz = Math.min(pointA.getZ(), pointB.getZ());
        return new Vector(minx, miny, minz);
    }

    public Vector getMax() {
        double maxx = Math.max(pointA.getX(), pointB.getX());
        double maxy = Math.max(pointA.getY(), pointB.getY());
        double maxz = Math.max(pointA.getZ(), pointB.getZ());
        return new Vector(maxx + 1, maxy, maxz + 1);
    }

    public Location getBlock(int index) {
        if (index != 0) {
            index -= 1;
        }
        Vector max = getMax();
        Vector min = getMin();
        int width = max.getBlockX() - min.getBlockX();
        int height = max.getBlockZ() - min.getBlockZ();
        int area = width * height;
        int level = 0;
        int row = 0;
        int extra = 0;
        if (index >= area) {
            level = Math.floorDiv(index, area);
            int remaining = Math.floorMod(index, area);
            row = Math.floorDiv(remaining, width);
            extra = Math.floorMod(index, width);
        } else if (index >= width) {
            row = Math.floorDiv(index, width);
            extra = Math.floorMod(index, width);
        } else if (index < width) {
            extra = index;
        }
        return new Location(GameManager.gameMapWorld, min.getBlockX() + extra, min.getBlockY() + level, min.getBlockZ() + row);
    }

    public boolean isInside(Location location) {
        return isInside(location, 0);
    }

    public boolean isInside(Location location, int extra) {
        World world = GameManager.gameMapWorld;
        Vector max = getMax();
        Vector min = getMin();

        return world.getName().equals(location.getWorld().getName())
                && location.getX() >= min.getX() - extra && location.getX() < max.getX() + extra
                && location.getZ() >= min.getZ() - extra && location.getZ() < max.getZ() + extra
                && location.getY() >= min.getY() - extra && location.getY() < max.getY() + extra;
    }


    public Collection<Location> getContainedBlocks() {
        List<Location> locations = new ArrayList<>();
        for (int x = getMin().getBlockX(); x <= getMax().getBlockX(); x++) {
            for (int y = getMin().getBlockY(); y <= getMax().getBlockY(); y++) {
                for (int z = getMin().getBlockZ(); z <= getMax().getBlockZ(); z++) {
                    Location location = new Location(GameManager.gameMapWorld, x, y, z);
                    locations.add(location);
                }
            }
        }
        return locations;
    }

}
