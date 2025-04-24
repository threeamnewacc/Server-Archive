// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import org.bukkit.Material;
import java.util.HashMap;
import org.bukkit.block.Block;
import org.bukkit.Location;
import java.util.Map;
import club.minemen.practice.arena.Arena;
import club.minemen.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class DuplicateArenaRunnable extends BukkitRunnable
{
    private final Practice plugin;
    private Arena copiedArena;
    private int offsetX;
    private int offsetZ;
    private int incrementX;
    private int incrementZ;
    private Map<Location, Block> paste;
    
    public DuplicateArenaRunnable(final Practice plugin, final Arena copiedArena, final int offsetX, final int offsetZ, final int incrementX, final int incrementZ) {
        this.plugin = plugin;
        this.copiedArena = copiedArena;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.incrementX = incrementX;
        this.incrementZ = incrementZ;
    }
    
    public void run() {
        if (this.paste == null) {
            final Map<Location, Block> copy = this.blocksFromTwoPoints(this.copiedArena.getMin().toBukkitLocation(), this.copiedArena.getMax().toBukkitLocation());
            this.paste = new HashMap<Location, Block>();
            for (final Location loc : copy.keySet()) {
                if (copy.get(loc).getType() != Material.AIR) {
                    this.paste.put(loc.clone().add((double)this.offsetX, 0.0, (double)this.offsetZ), copy.get(loc));
                }
            }
            copy.clear();
        }
        else {
            final Map<Location, Block> newPaste = new HashMap<Location, Block>();
            for (final Location loc : this.paste.keySet()) {
                if (this.paste.get(loc).getType() != Material.AIR) {
                    newPaste.put(loc.clone().add((double)this.incrementX, 0.0, (double)this.incrementZ), this.paste.get(loc));
                }
            }
            this.paste.clear();
            this.paste.putAll(newPaste);
        }
        boolean safe = true;
        for (final Location loc : this.paste.keySet()) {
            final Block block = loc.getBlock();
            if (block.getType() != Material.AIR) {
                safe = false;
                break;
            }
        }
        if (!safe) {
            this.offsetX += this.incrementX;
            this.offsetZ += this.incrementZ;
            this.run();
            return;
        }
        new BlockPlaceRunnable(this.paste, 5000) {
            @Override
            public void finish() {
                DuplicateArenaRunnable.this.onComplete();
            }
        }.runTaskTimer((Plugin)this.plugin, 0L, 5L);
    }
    
    public Map<Location, Block> blocksFromTwoPoints(final Location loc1, final Location loc2) {
        final Map<Location, Block> blocks = new HashMap<Location, Block>();
        final int topBlockX = (loc1.getBlockX() < loc2.getBlockX()) ? loc2.getBlockX() : loc1.getBlockX();
        final int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX()) ? loc2.getBlockX() : loc1.getBlockX();
        final int topBlockY = (loc1.getBlockY() < loc2.getBlockY()) ? loc2.getBlockY() : loc1.getBlockY();
        final int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY()) ? loc2.getBlockY() : loc1.getBlockY();
        final int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ()) ? loc2.getBlockZ() : loc1.getBlockZ();
        final int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ()) ? loc2.getBlockZ() : loc1.getBlockZ();
        for (int x = bottomBlockX; x <= topBlockX; ++x) {
            for (int z = bottomBlockZ; z <= topBlockZ; ++z) {
                for (int y = bottomBlockY; y <= topBlockY; ++y) {
                    final Block block = loc1.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        blocks.put(new Location(loc1.getWorld(), (double)x, (double)y, (double)z), block);
                    }
                }
            }
        }
        return blocks;
    }
    
    public abstract void onComplete();
    
    public Practice getPlugin() {
        return this.plugin;
    }
    
    public Arena getCopiedArena() {
        return this.copiedArena;
    }
    
    public int getOffsetX() {
        return this.offsetX;
    }
    
    public int getOffsetZ() {
        return this.offsetZ;
    }
    
    public int getIncrementX() {
        return this.incrementX;
    }
    
    public int getIncrementZ() {
        return this.incrementZ;
    }
    
    public Map<Location, Block> getPaste() {
        return this.paste;
    }
}
