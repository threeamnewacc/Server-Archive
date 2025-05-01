package net.badlion.shards.type;

import org.bukkit.Location;

public class Border {

    private String name;

    private int minX;
    private int minZ;

    private int maxX;
    private int maxZ;


    public Border(int x1, int z1, int x2, int z2) {
        this.minX = Math.min(x1, x2);
        this.minZ = Math.min(z1, z2);

        this.maxX = Math.max(x1, x2);
        this.maxZ = Math.max(z1, z2);
    }

    // Check if they are within the basic border edge not inside buffer
    public boolean isWithinBorder(Location location) {
        return location.getX() > minX && location.getX() <= maxX && location.getZ() > minZ && location.getZ() <= maxZ;
    }

    // 32 blocks of "buffer" zone
    public boolean isInside(Location location) {
        return location.getX() > (minX + 32.0) && location.getX() <= (maxX - 32.0) && location.getZ() > (minZ + 32.0) && location.getZ() <= (maxZ - 32.0);
    }

    // 64 blocks from the edge of where players switch to this server so 96 blocks, this is for synced entities
    public boolean isInsideNonRenderZone(Location location) {
        return location.getX() > (minX + 96.0) && location.getX() <= (maxX - 96.0) && location.getZ() > (minZ + 96.0) && location.getZ() <= (maxZ - 96.0);
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinZ() {
        return minZ;
    }
}
