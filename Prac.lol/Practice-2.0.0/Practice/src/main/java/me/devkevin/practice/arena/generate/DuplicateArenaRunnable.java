package me.devkevin.practice.arena.generate;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;

import java.util.HashMap;
import java.util.Map;


/**
 * @since 12/31/2021
 */
@Getter
public abstract class DuplicateArenaRunnable extends BukkitRunnable {

    private final Practice plugin;
    private Arena copiedArena;
    private int offsetX;
    private int offsetZ;
    private int incrementX;
    private int incrementZ;
    private Map<Location, Block> paste;

    public DuplicateArenaRunnable(Practice plugin, Arena copiedArena, int offsetX, int offsetZ, int incrementX, int incrementZ) {
        this.plugin = plugin;
        this.copiedArena = copiedArena;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
        this.incrementX = incrementX;
        this.incrementZ = incrementZ;
    }

    @Override
    public void run() {
        if (this.paste == null) {
            Map<Location, Block> copy = this.blocksFromTwoPoints(this.copiedArena.getMin().toBukkitLocation(), this.copiedArena.getMax().toBukkitLocation());
            this.paste = new HashMap<>();
            for (Location loc : copy.keySet()) {
                if (copy.get(loc).getType() != Material.AIR) {
                    this.paste.put(loc.clone().add(this.offsetX, 0, this.offsetZ), copy.get(loc));
                }
            }
            copy.clear();
        } else {
            Map<Location, Block> newPaste = new HashMap<>();
            for (Location loc : this.paste.keySet()) {
                if (this.paste.get(loc).getType() != Material.AIR) {
                    newPaste.put(loc.clone().add(this.incrementX, 0, this.incrementZ), this.paste.get(loc));
                }
            }
            this.paste.clear();
            this.paste.putAll(newPaste);
        }

        boolean safe = true;
        for (Location loc : this.paste.keySet()) {
            Block block = loc.getBlock();
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

        new BlockPlaceRunnable(this.copiedArena.getA().toBukkitLocation().getWorld(), this.paste) {
            @Override
            public void finish() {
                DuplicateArenaRunnable.this.onComplete();
            }
        }.runTaskTimer(this.plugin, 0L, 5L);
    }

    public Map<Location, Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        Map<Location, Block> blocks = new HashMap<>();

        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());

        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());

        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR) {
                        blocks.put(new Location(loc1.getWorld(), x, y, z), block);
                    }
                }
            }
        }

        return blocks;
    }

    public abstract void onComplete();
}
