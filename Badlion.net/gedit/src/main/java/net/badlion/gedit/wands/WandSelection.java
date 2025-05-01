package net.badlion.gedit.wands;

import net.badlion.gedit.BlockData;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.BlockVector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WandSelection {

    private UUID selector;

    private Location point1;

    private Location point2;

    public WandSelection(UUID selector) {
        this.selector = selector;
    }

    public void setPoint1(Location loc) {
        this.point1 = loc;
    }

    public void setPoint2(Location loc) {
        this.point2 = loc;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public List<Block> getAllBlocks() {
        List<Block> blocks = new ArrayList<Block>();

        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());

        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = point1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public BlockData[][][] getBlockDataArray() {
        BlockData[][][] blockDatas = new BlockData[getWidth()][getHeight()][getLength()];

        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());

        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
                for (int z = 0; z < getLength(); ++z) {
                    Block block = point1.getWorld().getBlockAt(minX + x, minY + y, minZ + z);
                    blockDatas[x][y][z] = new BlockData(new BlockVector(x, y, z), block.getTypeId(), block.getData());
                }
            }
        }
        return blockDatas;
    }

    public Location getMinPoint() {
        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());

        return new Location(point1.getWorld(), minX, minY, minZ);
    }

    public Location getMaxPoint() {
        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        int maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());

        return new Location(point1.getWorld(), maxX, maxY, maxZ);
    }

    public int getWidth() {
        int minX = Math.min(point1.getBlockX(), point2.getBlockX());
        int maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        return (maxX - minX) + 1;
    }

    public int getHeight() {
        int minY = Math.min(point1.getBlockY(), point2.getBlockY());
        int maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        return (maxY - minY) + 1;
    }

    public int getLength() {
        int minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());
        int maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
        return (maxZ - minZ) + 1;
    }

    public void clear() {
        this.point1 = null;
        this.point2 = null;
    }


    public boolean isValidSelection() {
        return this.point1 != null && this.point2 != null && this.point1.getWorld().getName().equals(this.point2.getWorld().getName());
    }

}
