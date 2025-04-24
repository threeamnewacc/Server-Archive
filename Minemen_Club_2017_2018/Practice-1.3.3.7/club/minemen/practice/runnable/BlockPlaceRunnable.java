// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Iterator;
import org.bukkit.block.Block;
import org.bukkit.Location;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class BlockPlaceRunnable extends BukkitRunnable
{
    private final ConcurrentMap<Location, Block> blocks;
    private final int bps;
    private final int totalBlocks;
    private final Iterator<Location> iterator;
    private int blockIndex;
    private int blocksPlaced;
    private boolean completed;
    
    public BlockPlaceRunnable(final Map<Location, Block> blocks, final int bps) {
        this.blockIndex = 0;
        this.blocksPlaced = 0;
        this.completed = false;
        (this.blocks = new ConcurrentHashMap<Location, Block>()).putAll((Map<?, ?>)blocks);
        this.bps = bps;
        this.totalBlocks = blocks.keySet().size();
        this.iterator = blocks.keySet().iterator();
    }
    
    public void run() {
        if (this.blocks.isEmpty() || !this.iterator.hasNext()) {
            this.finish();
            this.completed = true;
            this.cancel();
            return;
        }
        while (this.iterator.hasNext()) {
            if (this.blockIndex >= this.bps) {
                this.blockIndex = 0;
                break;
            }
            final Location loc = this.iterator.next();
            final Block block = this.blocks.get(loc);
            if (!loc.getWorld().getChunkAt(loc).isLoaded()) {
                loc.getWorld().getChunkAt(loc).load();
            }
            loc.getBlock().setType(block.getType());
            loc.getBlock().setData(block.getData());
            loc.getBlock().getState().setType(block.getType());
            loc.getBlock().getState().setData(block.getState().getData());
            loc.getBlock().getState().update();
            this.blocks.remove(loc);
            ++this.blocksPlaced;
            ++this.blockIndex;
        }
    }
    
    public abstract void finish();
    
    public ConcurrentMap<Location, Block> getBlocks() {
        return this.blocks;
    }
    
    public int getBps() {
        return this.bps;
    }
    
    public int getTotalBlocks() {
        return this.totalBlocks;
    }
    
    public Iterator<Location> getIterator() {
        return this.iterator;
    }
    
    public int getBlockIndex() {
        return this.blockIndex;
    }
    
    public int getBlocksPlaced() {
        return this.blocksPlaced;
    }
    
    public boolean isCompleted() {
        return this.completed;
    }
}
