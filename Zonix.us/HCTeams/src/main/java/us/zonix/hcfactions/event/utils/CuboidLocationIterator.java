package us.zonix.hcfactions.event.utils;

import java.util.*;
import org.bukkit.*;

public class CuboidLocationIterator implements Iterator<Location>
{
    private final World world;
    private final int baseX;
    private final int baseY;
    private final int baseZ;
    private final int sizeX;
    private final int sizeY;
    private final int sizeZ;
    private int x;
    private int y;
    private int z;
    
    public CuboidLocationIterator(final World world, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
        this.world = world;
        this.baseX = x1;
        this.baseY = y1;
        this.baseZ = z1;
        this.sizeX = Math.abs(x2 - x1) + 1;
        this.sizeY = Math.abs(y2 - y1) + 1;
        this.sizeZ = Math.abs(z2 - z1) + 1;
        final boolean x3 = false;
        this.z = 0;
        this.y = 0;
        this.x = 0;
    }
    
    @Override
    public boolean hasNext() {
        return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
    }
    
    @Override
    public Location next() {
        final Location location = new Location(this.world, (double)(this.baseX + this.x), (double)(this.baseY + this.y), (double)(this.baseZ + this.z));
        if (++this.x >= this.sizeX) {
            this.x = 0;
            if (++this.y >= this.sizeY) {
                this.y = 0;
                ++this.z;
            }
        }
        return location;
    }
    
    @Override
    public void remove() {
    }
}
