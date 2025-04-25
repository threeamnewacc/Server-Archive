package us.zonix.hcfactions.event.utils;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import us.zonix.hcfactions.util.player.PlayerUtility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Cuboid implements Iterable<Block>, Cloneable, ConfigurationSerializable {
    private static final int CHUNK_SIZE = 16;
    protected final String worldName;
    protected final Location locationOne;
    protected final Location locationTwo;
    protected int x1;
    protected int y1;
    protected int z1;
    protected int x2;
    protected int y2;
    protected int z2;

    public Cuboid(final Map<String, Object> map) {
        this.worldName = (String) map.get("worldName");
        this.locationOne = null;
        this.locationTwo =  null;
        this.x1 = (Integer) map.get("x1");
        this.y1 = (Integer) map.get("y1");
        this.z1 = (Integer) map.get("z1");
        this.x2 = (Integer) map.get("x2");
        this.y2 = (Integer) map.get("y2");
        this.z2 = (Integer) map.get("z2");
    }

    public Cuboid(final World world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this(((World) Preconditions.checkNotNull((Object) world)).getName(), x1, y1, z1, x2, y2, z2);
    }

    private Cuboid(final String worldName, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        Preconditions.checkNotNull((Object) worldName, "World name cannot be null");
        this.worldName = worldName;
        this.locationOne = null;
        this.locationTwo =  null;
        this.x1 = Math.min(x1, x2);
        this.y1 = Math.min(y1, y2);
        this.z1 = Math.min(z1, z2);
        this.x2 = Math.max(x1, x2);
        this.y2 = Math.max(y1, y2);
        this.z2 = Math.max(z1, z2);
    }

    public Cuboid(final Location first, final Location second) {
        Preconditions.checkNotNull((Object) first, "Location 1 cannot be null");
        Preconditions.checkNotNull((Object) second, "Location 2 cannot be null");
        Preconditions.checkArgument(first.getWorld().equals(second.getWorld()), "Locations must be on the same world");
        this.worldName = first.getWorld().getName();
        this.locationOne = first;
        this.locationTwo = second;
        this.x1 = Math.min(first.getBlockX(), second.getBlockX());
        this.y1 = Math.min(first.getBlockY(), second.getBlockY());
        this.z1 = Math.min(first.getBlockZ(), second.getBlockZ());
        this.x2 = Math.max(first.getBlockX(), second.getBlockX());
        this.y2 = Math.max(first.getBlockY(), second.getBlockY());
        this.z2 = Math.max(first.getBlockZ(), second.getBlockZ());
    }

    public Cuboid(final Location location) {
        this(location, location);
    }

    public Cuboid(final Cuboid other) {
        this(other.getWorld().getName(), other.x1, other.y1, other.z1, other.x2, other.y2, other.z2);
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();
        map.put("worldName", this.worldName);
        map.put("x1", this.x1);
        map.put("y1", this.y1);
        map.put("z1", this.z1);
        map.put("x2", this.x2);
        map.put("y2", this.y2);
        map.put("z2", this.z2);
        return map;
    }

    public boolean hasBothPositionsSet() {
        return this.getMinimumPoint() != null && this.getMaximumPoint() != null;
    }

    public int getMinimumX() {
        return Math.min(this.x1, this.x2);
    }

    public int getMinimumZ() {
        return Math.min(this.z1, this.z2);
    }

    public int getMaximumX() {
        return Math.max(this.x1, this.x2);
    }

    public int getMaximumZ() {
        return Math.max(this.z1, this.z2);
    }

    public List<Vector> edges() {
        final Location v1 = this.getMinimumPoint();
        final Location v2 = this.getMaximumPoint();
        final int minX = v1.getBlockX();
        final int maxX = v2.getBlockX();
        final int minZ = v1.getBlockZ();
        final int startX = minZ + 1;
        final int maxZ = v2.getBlockZ();
        int capacity = (maxX - minX) * 4 + (maxZ - minZ) * 4;
        capacity += 4;
        final List<Vector> result = new ArrayList<>(capacity);
        if(capacity <= 0) {
            return result;
        }
        final int minY = v1.getBlockY();
        final int maxY = v1.getBlockY();
        for(int x = minX; x <= maxX; ++x) {
            result.add(new Vector(x, minY, minZ));
            result.add(new Vector(x, minY, maxZ));
            result.add(new Vector(x, maxY, minZ));
            result.add(new Vector(x, maxY, maxZ));
        }
        for(int z = startX; z < maxZ; ++z) {
            result.add(new Vector(minX, minY, z));
            result.add(new Vector(minX, maxY, z));
            result.add(new Vector(maxX, minY, z));
            result.add(new Vector(maxX, maxY, z));
        }
        return result;
    }

    public Set<Player> getPlayers() {
        final Set<Player> players = new HashSet<>();
        for(final Player player : PlayerUtility.getOnlinePlayers() ) {
            if(this.contains(player)) {
                players.add(player);
            }
        }
        return players;
    }

    public Location getLowerNE() {
        return new Location(this.getWorld(), (double) this.x1, (double) this.y1, (double) this.z1);
    }

    public Location getUpperSW() {
        return new Location(this.getWorld(), (double) this.x2, (double) this.y2, (double) this.z2);
    }

    public Location getCenter() {
        final int x1 = this.x2 + 1;
        final int y1 = this.y2 + 1;
        final int z1 = this.z2 + 1;
        return new Location(this.getWorld(), this.x1 + (x1 - this.x1) / 2.0, this.y1 + (y1 - this.y1) / 2.0, this.z1 + (z1 - this.z1) / 2.0);
    }

    public String getWorldName() {
        return this.worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.worldName);
    }

    public int getSizeX() {
        return this.x2 - this.x1 + 1;
    }

    public int getSizeY() {
        return this.y2 - this.y1 + 1;
    }

    public int getSizeZ() {
        return this.z2 - this.z1 + 1;
    }

    public int getX1() {
        return this.x1;
    }

    public void setX1(final int x1) {
        this.x1 = x1;
    }

    public int getY1() {
        return this.y1;
    }

    public void setY1(final int y1) {
        this.y1 = y1;
    }

    public int getZ1() {
        return this.z1;
    }

    public void setZ1(final int z1) {
        this.z1 = z1;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }

    public void setY2(final int y2) {
        this.y2 = y2;
    }

    public int getZ2() {
        return this.z2;
    }

    public Location[] getCornerLocations() {
        final Location[] result = new Location[8];
        final Block[] cornerBlocks = this.getCornerBlocks();
        for(int i = 0; i < cornerBlocks.length; ++i) {
            result[i] = cornerBlocks[i].getLocation();
        }
        return result;
    }

    public Block[] getCornerBlocks() {
        final Block[] result = new Block[8];
        final World world = this.getWorld();
        result[0] = world.getBlockAt(this.x1, this.y1, this.z1);
        result[1] = world.getBlockAt(this.x1, this.y1, this.z2);
        result[2] = world.getBlockAt(this.x1, this.y2, this.z1);
        result[3] = world.getBlockAt(this.x1, this.y2, this.z2);
        result[4] = world.getBlockAt(this.x2, this.y1, this.z1);
        result[5] = world.getBlockAt(this.x2, this.y1, this.z2);
        result[6] = world.getBlockAt(this.x2, this.y2, this.z1);
        result[7] = world.getBlockAt(this.x2, this.y2, this.z2);
        return result;
    }

    public Cuboid expand(final CuboidDirection dir, final int amount) {
        switch(dir) {
            case NORTH: {
                return new Cuboid(this.worldName, this.x1 - amount, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2 + amount, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1 - amount, this.x2, this.y2, this.z2);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z2 + amount);
            }
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1 - amount, this.z1, this.x2, this.y2, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2 + amount, this.z2);
            }
            default: {
                throw new IllegalArgumentException("invalid direction " + dir);
            }
        }
    }

    public Cuboid shift(final CuboidDirection dir, final int amount) {
        return this.expand(dir, amount).expand(dir.opposite(), -amount);
    }

    public Cuboid outset(final CuboidDirection dir, final int amount) {
        switch(dir) {
            case HORIZONTAL: {
                return this.expand(CuboidDirection.NORTH, amount).expand(CuboidDirection.SOUTH, amount).expand(CuboidDirection.EAST, amount).expand(CuboidDirection.WEST, amount);
            }
            case VERTICAL: {
                return this.expand(CuboidDirection.DOWN, amount).expand(CuboidDirection.UP, amount);
            }
            case BOTH: {
                return this.outset(CuboidDirection.HORIZONTAL, amount).outset(CuboidDirection.VERTICAL, amount);
            }
            default: {
                throw new IllegalArgumentException("invalid direction " + dir);
            }
        }
    }

    public Cuboid inset(final CuboidDirection direction, final int amount) {
        return this.outset(direction, -amount);
    }

    public boolean contains(final Cuboid cuboid) {
        return this.contains(cuboid.getMinimumPoint()) || this.contains(cuboid.getMaximumPoint());
    }

    public boolean contains(final Player player) {
        return this.contains(player.getLocation());
    }

    public boolean contains(final World world, final int x, final int z) {
        return (world == null || this.getWorld().equals(world)) && x >= this.x1 && x <= this.x2 && z >= this.z1 && z <= this.z2;
    }

    public boolean contains(final int x, final int y, final int z) {
        return x >= this.x1 && x <= this.x2 && y >= this.y1 && y <= this.y2 && z >= this.z1 && z <= this.z2;
    }

    public boolean contains(final Block block) {
        return this.contains(block.getLocation());
    }

    public boolean contains(final Location location) {
        if(location == null || this.worldName == null) {
            return false;
        }
        final World world = location.getWorld();
        return world != null && this.worldName.equals(location.getWorld().getName()) && this.contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getVolume() {
        return this.getSizeX() * this.getSizeY() * this.getSizeZ();
    }

    public int getArea() {
        final Location min = this.getMinimumPoint();
        final Location max = this.getMaximumPoint();
        return (max.getBlockX() - min.getBlockX() + 1) * (max.getBlockZ() - min.getBlockZ() + 1);
    }

    public byte getAverageLightLevel() {
        long total = 0L;
        int count = 0;
        for(final Block block : this) {
            if(block.isEmpty()) {
                total += block.getLightLevel();
                ++count;
            }
        }
        return (count > 0) ? ((byte) (total / count)) : 0;
    }

    public Location getMinimumPoint() {
        return new Location(this.getWorld(), (double) Math.min(this.x1, this.x2), (double) Math.min(this.y1, this.y2), (double) Math.min(this.z1, this.z2));
    }

    public Location getMaximumPoint() {
        return new Location(this.getWorld(), (double) Math.max(this.x1, this.x2), (double) Math.max(this.y1, this.y2), (double) Math.max(this.z1, this.z2));
    }

    public Location getLocationOne() {
        return locationOne;
    }

    public Location getLocationTwo() {
        return locationTwo;
    }

    public int getWidth() {
        return this.getMaximumPoint().getBlockX() - this.getMinimumPoint().getBlockX();
    }

    public int getHeight() {
        return this.getMaximumPoint().getBlockY() - this.getMinimumPoint().getBlockY();
    }

    public int getLength() {
        return this.getMaximumPoint().getBlockZ() - this.getMinimumPoint().getBlockZ();
    }

    public Cuboid contract() {
        return this.contract(CuboidDirection.DOWN).contract(CuboidDirection.SOUTH).contract(CuboidDirection.EAST).contract(CuboidDirection.UP).contract(CuboidDirection.NORTH).contract(CuboidDirection.WEST);
    }

    public Cuboid contract(final CuboidDirection direction) {
        Cuboid face = this.getFace(direction.opposite());
        switch(direction) {
            case DOWN: {
                while(face.containsOnly(Material.AIR) && face.y1 > this.y1) {
                    face = face.shift(CuboidDirection.DOWN, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, face.y2, this.z2);
            }
            case UP: {
                while(face.containsOnly(Material.AIR) && face.y2 < this.y2) {
                    face = face.shift(CuboidDirection.UP, 1);
                }
                return new Cuboid(this.worldName, this.x1, face.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                while(face.containsOnly(Material.AIR) && face.x1 > this.x1) {
                    face = face.shift(CuboidDirection.NORTH, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, face.x2, this.y2, this.z2);
            }
            case SOUTH: {
                while(face.containsOnly(Material.AIR) && face.x2 < this.x2) {
                    face = face.shift(CuboidDirection.SOUTH, 1);
                }
                return new Cuboid(this.worldName, face.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                while(face.containsOnly(Material.AIR) && face.z1 > this.z1) {
                    face = face.shift(CuboidDirection.EAST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, face.z2);
            }
            case WEST: {
                while(face.containsOnly(Material.AIR) && face.z2 < this.z2) {
                    face = face.shift(CuboidDirection.WEST, 1);
                }
                return new Cuboid(this.worldName, this.x1, this.y1, face.z1, this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    public Cuboid getFace(final CuboidDirection direction) {
        switch(direction) {
            case DOWN: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y1, this.z2);
            }
            case UP: {
                return new Cuboid(this.worldName, this.x1, this.y2, this.z1, this.x2, this.y2, this.z2);
            }
            case NORTH: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x1, this.y2, this.z2);
            }
            case SOUTH: {
                return new Cuboid(this.worldName, this.x2, this.y1, this.z1, this.x2, this.y2, this.z2);
            }
            case EAST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z1, this.x2, this.y2, this.z1);
            }
            case WEST: {
                return new Cuboid(this.worldName, this.x1, this.y1, this.z2, this.x2, this.y2, this.z2);
            }
            default: {
                throw new IllegalArgumentException("Invalid direction " + direction);
            }
        }
    }

    public boolean containsOnly(final Material material) {
        for(final Block block : this) {
            if(block.getType() != material) {
                return false;
            }
        }
        return true;
    }

    public Cuboid getBoundingCuboid(final Cuboid other) {
        if(other == null) {
            return this;
        }
        final int xMin = Math.min(this.x1, other.x1);
        final int yMin = Math.min(this.y1, other.y1);
        final int zMin = Math.min(this.z1, other.z1);
        final int xMax = Math.max(this.x2, other.x2);
        final int yMax = Math.max(this.y2, other.y2);
        final int zMax = Math.max(this.z2, other.z2);
        return new Cuboid(this.worldName, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public Block getRelativeBlock(final int x, final int y, final int z) {
        return this.getWorld().getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public Block getRelativeBlock(final World world, final int x, final int y, final int z) {
        return world.getBlockAt(this.x1 + x, this.y1 + y, this.z1 + z);
    }

    public List<Chunk> getChunks() {
        final World world = this.getWorld();
        final int x1 = this.x1 & 0xFFFFFFF0;
        final int x2 = this.x2 & 0xFFFFFFF0;
        final int z1 = this.z1 & 0xFFFFFFF0;
        final int z2 = this.z2 & 0xFFFFFFF0;
        final List<Chunk> result = new ArrayList<>(x2 - x1 + 16 + (z2 - z1) * 16);
        for(int x3 = x1; x3 <= x2; x3 += 16) {
            for(int z3 = z1; z3 <= z2; z3 += 16) {
                result.add(world.getChunkAt(x3 >> 4, z3 >> 4));
            }
        }
        return result;
    }

    @Override
    public Iterator<Block> iterator() {
        return new CuboidBlockIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Iterator<Location> locationIterator() {
        return new CuboidLocationIterator(this.getWorld(), this.x1, this.y1, this.z1, this.x2, this.y2, this.z2);
    }

    public Cuboid clone() {
        try {
            return (Cuboid) super.clone();
        } catch(CloneNotSupportedException e) {
            throw new RuntimeException("this could never happen", e);
        }
    }

    @Override
    public String toString() {
        return "Cuboid: " + this.worldName + ',' + this.x1 + ',' + this.y1 + ',' + this.z1 + "=>" + this.x2 + ',' + this.y2 + ',' + this.z2;
    }
}
