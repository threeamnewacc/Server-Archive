package us.zonix.practice.runnable;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class BlockPlaceRunnable extends BukkitRunnable {
    private World world;
    private final ConcurrentMap<Location, Block> blocks;
    private final int totalBlocks;
    private final Iterator<Location> iterator;
    private int blockIndex = 0;
    private int blocksPlaced = 0;
    private boolean completed = false;

    public BlockPlaceRunnable(World world, Map<Location, Block> blocks) {
        this.world = world;
        this.blocks = new ConcurrentHashMap<>();
        this.blocks.putAll(blocks);
        this.totalBlocks = blocks.keySet().size();
        this.iterator = blocks.keySet().iterator();
    }

    public void run() {
        if (!this.blocks.isEmpty() && this.iterator.hasNext()) {
            TaskManager.IMP
                .async(
                    () -> {
                        EditSession editSession = new EditSessionBuilder(this.world.getName())
                            .fastmode(true)
                            .allowedRegionsEverywhere()
                            .autoQueue(false)
                            .limitUnlimited()
                            .build();

                        for (Entry<Location, Block> entry : this.blocks.entrySet()) {
                            try {
                                editSession.setBlock(
                                    new Vector((double)entry.getKey().getBlockX(), (double)entry.getKey().getBlockY(), entry.getKey().getZ()),
                                    new BaseBlock(entry.getValue().getTypeId(), entry.getValue().getData())
                                );
                            } catch (Exception var5) {
                            }
                        }

                        editSession.flushQueue();
                        TaskManager.IMP.task(this.blocks::clear);
                    }
                );
        } else {
            this.finish();
            this.completed = true;
            this.cancel();
        }
    }

    public abstract void finish();

    public World getWorld() {
        return this.world;
    }

    public ConcurrentMap<Location, Block> getBlocks() {
        return this.blocks;
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
