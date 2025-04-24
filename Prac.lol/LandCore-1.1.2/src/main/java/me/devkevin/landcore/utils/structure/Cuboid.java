package me.devkevin.landcore.utils.structure;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;

import java.util.*;

/**
 * Represents a cuboid region from one location to another.
 *
 * @skidder Coords
 */
public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {
    private final String worldName;
    private final int x1, y1, z1;
    private final int x2, y2, z2;

    public Cuboid(Location locationA, Location locationB) {
        if (!locationA.getWorld().equals(locationB.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }

        this.worldName = locationA.getWorld().getName();
        this.x1 = Math.min(locationA.getBlockX(), locationB.getBlockX());
        this.y1 = Math.min(locationA.getBlockY(), locationB.getBlockY());
        this.z1 = Math.min(locationA.getBlockZ(), locationB.getBlockZ());
        this.x2 = Math.max(locationA.getBlockX(), locationB.getBlockX());
        this.y2 = Math.max(locationA.getBlockY(), locationB.getBlockY());
        this.z2 = Math.max(locationA.getBlockZ(), locationB.getBlockZ());
    }

    public Cuboid(Location location) {
        this(location, location);
    }

    public Cuboid(Cuboid other) {
        this(other.worldName, other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
    }

    public Cuboid(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = world.getName();
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    private Cuboid(String worldName, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = worldName;
        this.x1 = Math.min(x1, x2);
        this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.z2 = Math.max(z1, z2);
    }

    public Cuboid(Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");
        this.x1 = (Integer) map.get("x1");
        this.x2 = (Integer) map.get("x2");
        this.y1 = (Integer) map.get("y1");
        this.y2 = (Integer) map.get("y2");
        this.z1 = (Integer) map.get("z1");
        this.z2 = (Integer) map.get("z2");
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("worldName", this.worldName);
        map.put("x1", this.x1);
        map.put("y1", this.y1);
        map.put("z1", this.z1);
        map.put("x2", this.x2);
        map.put("y2", this.y2);
        map.put("z2", this.z2);

        return map;
    }

    public Location getLowerNorthEast() {
        return new Location(this.getWorld(), this.x1, this.y1, this.z1);
    }

    public Location getUpperSouthWest() {
        return new Location(this.getWorld(), this.x2, this.y2, this.z2);
    }

    public List<Block> getBlocks() {
        Iterator<Block> blockIterator = this.iterator();
        List<Block> copy = new ArrayList<>();

        while (blockIterator.hasNext()) {
            copy.add(blockIterator.next());
        }

        return copy;
    }

    public Location getCenter() {
        int x1 = this.getUpperX() + 1;
        int y1 = this.getUpperY() + 1;
        int z1 = this.getUpperZ() + 1;

        return new Location(
                this.getWorld(),
                this.getLowerX() + (x1 - this.getLowerX()) / 2.0,
                this.getLowerY() + (y1 - this.getLowerY()) / 2.0,
                this.getLowerZ() + (z1 - this.getLowerZ()) / 2.0
        );
    }

    public void place(Location location) {
        place(location, false);
    }

    @SuppressWarnings("deprecation")
    public void place(Location location, boolean skipAir) {
        World world = getWorld();
        Location center = getCenter();
        int diffX = location.getBlockX() - center.getBlockX();
        int diffZ = location.getBlockZ() - center.getBlockZ();

        for (Block block : this) {
            Material blockType = block.getType();

            if (skipAir && blockType == Material.AIR) {
                continue;
            }

            Block placed = world.getBlockAt(block.getX() + diffX, block.getY(), block.getZ() + diffZ);

            ((CraftBlock) placed).setTypeIdAndData(blockType.getId(), block.getData(), false);
        }
    }

    public World getWorld() {
        World world = Bukkit.getWorld(this.worldName);

        if (world == null) {
            throw new IllegalStateException("World '" + this.worldName + "' is not loaded");
        }

        return world;
    }

    public int getSizeX() {
        return (this.x2 - this.x1) + 1;
    }

    public int getSizeY() {
        return (this.y2 - this.y1) + 1;
    }

    public int getSizeZ() {
        return (this.z2 - this.z1) + 1;
    }

    public int getLowerX() {
        return this.x1;
    }

    public int getLowerY() {
        return this.y1;
    }

    public int getLowerZ() {
        return this.z1;
    }

    public int getUpperX() {
        return this.x2;
    }

    public int getUpperY() {
        return this.y2;
    }

    public int getUpperZ() {
        return this.z2;
    }

    public Block[] getCorners() {
        Block[] cornerBlocks = new Block[8];
        World world = this.getWorld();

        cornerBlocks[0] = world.getBlockAt(this.x1, this.y1, this.z1);
        cornerBlocks[1] = world.getBlockAt(this.x1, this.y1, this.z2);
        cornerBlocks[2] = world.getBlockAt(this.x1, this.y2, this.z1);
        cornerBlocks[3] = world.getBlockAt(this.x1, this.y2, this.z2);
        cornerBlocks[4] = world.getBlockAt(this.x2, this.y1, this.z1);
        cornerBlocks[5] = world.getBlockAt(this.x2, this.y1, this.z2);
        cornerBlocks[6] = world.getBlockAt(this.x2, this.y2, this.z1);
        cornerBlocks[7] = world.getBlockAt(this.x2, this.y2, this.z2);

        return cornerBlocks;
    }

    public Cuboid expand(CuboidDirection direction, int amount) {
        switch (direction) {
            case NORTH:
                return new Cuboid(this.worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
            case SOUTH:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
            case EAST:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
            case WEST:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
            case DOWN:
                return new Cuboid(this.worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
            case UP:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
            default:
                throw new IllegalArgumentException("Invalid direction: " + direction);
        }
    }

    public Cuboid shift(CuboidDirection direction, int amount) {
        return expand(direction, amount).expand(direction.opposite(), -amount);
    }

    public Cuboid outset(CuboidDirection direction, int amount) {
        Cuboid cuboid;

        switch (direction) {
            case HORIZONTAL:
                cuboid = expand(CuboidDirection.NORTH, amount)
                        .expand(CuboidDirection.SOUTH, amount)
                        .expand(CuboidDirection.EAST, amount)
                        .expand(CuboidDirection.WEST, amount);
                break;
            case VERTICAL:
                cuboid = expand(CuboidDirection.DOWN, amount)
                        .expand(CuboidDirection.UP, amount);
                break;
            case BOTH:
                cuboid = outset(CuboidDirection.HORIZONTAL, amount)
                        .outset(CuboidDirection.VERTICAL, amount);
                break;
            default:
                throw new IllegalArgumentException("Invalid direction " + direction);
        }

        return cuboid;
    }

    public Cuboid inset(CuboidDirection dir, int amount) {
        return this.outset(dir, -amount);
    }

    public boolean contains(int x, int y, int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    public boolean contains(Block block) {
        return this.contains(block.getLocation());
    }

    public boolean contains(Location location) {
        if (!this.worldName.equals(location.getWorld().getName())) {
            return false;
        }

        return this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    public byte getAverageLightLevel() {
        long total = 0;
        int count = 0;

        for (Block block : this) {
            if (block.isEmpty()) {
                total += block.getLightLevel();
                ++count;
            }
        }

        return count > 0 ? (byte) (total / count) : 0;
    }

    public Cuboid contract() {
        return this.contract(CuboidDirection.DOWN)
                .contract(CuboidDirection.SOUTH)
                .contract(CuboidDirection.EAST)
                .contract(CuboidDirection.UP)
                .contract(CuboidDirection.NORTH)
                .contract(CuboidDirection.WEST);
    }

    public Cuboid contract(CuboidDirection direction) {
        Cuboid face = getFace(direction.opposite());

        switch (direction) {
            case DOWN:
                while (face.containsOnly(Material.AIR) && face.getLowerY() > this.getLowerY()) {
                    face = face.shift(CuboidDirection.DOWN, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, face.getUpperY(), this.z2);
            case UP:
                while (face.containsOnly(Material.AIR) && face.getUpperY() < this.getUpperY()) {
                    face = face.shift(CuboidDirection.UP, 1);
                }

                return new Cuboid(this.worldName, this.x1, face.getLowerY(), this.z1, this.x2, this.y2, this.z2);
            case NORTH:
                while (face.containsOnly(Material.AIR) && face.getLowerX() > this.getLowerX()) {
                    face = face.shift(CuboidDirection.NORTH, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, face.getUpperX(), this.y2, this.z2);
            case SOUTH:
                while (face.containsOnly(Material.AIR) && face.getUpperX() < this.getUpperX()) {
                    face = face.shift(CuboidDirection.SOUTH, 1);
                }

                return new Cuboid(this.worldName, face.getLowerX(), this.y1, this.z1, this.x2, this.y2, this.z2);
            case EAST:
                while (face.containsOnly(Material.AIR) && face.getLowerZ() > this.getLowerZ()) {
                    face = face.shift(CuboidDirection.EAST, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, face.getUpperZ());
            case WEST:
                while (face.containsOnly(Material.AIR) && face.getUpperZ() < this.getUpperZ()) {
                    face = face.shift(CuboidDirection.WEST, 1);
                }

                return new Cuboid(this.worldName, this.x1, this.y1, face.getLowerZ(), this.x2, this.y2, this.z2);
            default:
                throw new IllegalArgumentException("Invalid direction " + direction);
        }
    }

    public Cuboid getFace(CuboidDirection direction) {
        switch (direction) {
            case DOWN:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
            case UP:
                return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
            case NORTH:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
            case SOUTH:
                return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
            case EAST:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
            case WEST:
                return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
            default:
                throw new IllegalArgumentException("Invalid direction " + direction);
        }
    }

    public boolean containsOnly(Material type) {
        for (Block block : this) {
            if (block.getType() != type) {
                return false;
            }
        }

        return true;
    }

    public Cuboid getBoundingCuboid(Cuboid other) {
        if (other == null) {
            return this;
        }

        int xMin = Math.min(this.getLowerX(), other.getLowerX());
        int yMin = Math.min(this.getLowerY(), other.getLowerY());
        int zMin = Math.min(this.getLowerZ(), other.getLowerZ());
        int xMax = Math.max(this.getUpperX(), other.getUpperX());
        int yMax = Math.max(this.getUpperY(), other.getUpperY());
        int zMax = Math.max(this.getUpperZ(), other.getUpperZ());

        return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public Block getRelativeBlock(int x, int y, int z) {
        return this.getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public Block getRelativeBlock(World world, int x, int y, int z) {
        return world.getBlockAt(this.x1 + x, y1 + y, this.z1 + z);
    }

    public List<Chunk> getChunks() {
        List<Chunk> chunks = new ArrayList<>();
        World world = this.getWorld();

        int x1 = this.getLowerX() & ~0xf;
        int x2 = this.getUpperX() & ~0xf;
        int z1 = this.getLowerZ() & ~0xf;
        int z2 = this.getUpperZ() & ~0xf;

        for (int x = x1; x <= x2; x += 16) {
            for (int z = z1; z <= z2; z += 16) {
                chunks.add(world.getChunkAt(x >> 4, z >> 4));
            }
        }

        return chunks;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    @Override
    public Cuboid clone() {
        return new Cuboid(this);
    }

    @Override
    public String toString() {
        return "Cuboid{" +
                "worldName='" + worldName + '\'' +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", z1=" + z1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", z2=" + z2 +
                '}';
    }

    public enum CuboidDirection {
        NORTH, EAST, SOUTH, WEST, UP, DOWN, HORIZONTAL, VERTICAL, BOTH, UNKNOWN;

        public CuboidDirection opposite() {
            switch (this) {
                case NORTH:
                    return SOUTH;
                case EAST:
                    return WEST;
                case SOUTH:
                    return NORTH;
                case WEST:
                    return EAST;
                case HORIZONTAL:
                    return VERTICAL;
                case VERTICAL:
                    return HORIZONTAL;
                case UP:
                    return DOWN;
                case DOWN:
                    return UP;
                case BOTH:
                    return BOTH;
                default:
                    return UNKNOWN;
            }
        }
    }

    public class CuboidIterator implements Iterator<Block> {
        private World world;
        private int baseX, baseY, baseZ;
        private int x, y, z;
        private int sizeX, sizeY, sizeZ;

        public CuboidIterator(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
            this.world = world;
            this.baseX = x1;
            this.baseY = y1;
            this.baseZ = z1;
            this.sizeX = Math.abs(x2 - x1) + 1;
            this.sizeY = Math.abs(y2 - y1) + 1;
            this.sizeZ = Math.abs(z2 - z1) + 1;
            this.x = this.y = this.z = 0;
        }

        @Override
        public boolean hasNext() {
            return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
        }

        @Override
        public Block next() {
            Block block = this.world.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);

            if (++x >= this.sizeX) {
                this.x = 0;

                if (++this.y >= this.sizeY) {
                    this.y = 0;
                    ++this.z;
                }
            }

            return block;
        }

        @Override
        public void remove() {
        }
    }
}
