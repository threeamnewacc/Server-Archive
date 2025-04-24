// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import java.util.Iterator;
import club.minemen.core.util.CustomLocation;
import club.minemen.practice.arena.StandaloneArena;
import club.minemen.practice.arena.Arena;
import org.bukkit.plugin.Plugin;
import club.minemen.core.CorePlugin;
import club.minemen.practice.Practice;

public class ChunkManager
{
    private final Practice plugin;
    private boolean chunksLoaded;
    
    public ChunkManager() {
        this.plugin = Practice.getInstance();
        CorePlugin.getInstance().setSetupSupplier(() -> this.chunksLoaded);
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, this::loadChunks, 1L);
    }
    
    private void loadChunks() {
        this.plugin.getLogger().info("Started loading all the chunks...");
        final CustomLocation spawnMin = this.plugin.getSpawnManager().getSpawnMin();
        final CustomLocation spawnMax = this.plugin.getSpawnManager().getSpawnMax();
        if (spawnMin != null && spawnMax != null) {
            int spawnMinX = spawnMin.toBukkitLocation().getBlockX() >> 4;
            int spawnMinZ = spawnMin.toBukkitLocation().getBlockZ() >> 4;
            int spawnMaxX = spawnMax.toBukkitLocation().getBlockX() >> 4;
            int spawnMaxZ = spawnMax.toBukkitLocation().getBlockZ() >> 4;
            if (spawnMinX > spawnMaxX) {
                final int lastSpawnMinX = spawnMinX;
                spawnMinX = spawnMaxX;
                spawnMaxX = lastSpawnMinX;
            }
            if (spawnMinZ > spawnMaxZ) {
                final int lastSpawnMinZ = spawnMinZ;
                spawnMinZ = spawnMaxZ;
                spawnMaxZ = lastSpawnMinZ;
            }
            for (int x = spawnMinX; x <= spawnMaxX; ++x) {
                for (int z = spawnMinZ; z <= spawnMaxZ; ++z) {
                    spawnMin.toBukkitWorld().getChunkAt(x, z);
                }
            }
        }
        final CustomLocation editorMin = this.plugin.getSpawnManager().getEditorMin();
        final CustomLocation editorMax = this.plugin.getSpawnManager().getEditorMax();
        if (editorMin != null && editorMax != null) {
            int editorMinX = editorMin.toBukkitLocation().getBlockX() >> 4;
            int editorMinZ = editorMin.toBukkitLocation().getBlockZ() >> 4;
            int editorMaxX = editorMax.toBukkitLocation().getBlockX() >> 4;
            int editorMaxZ = editorMax.toBukkitLocation().getBlockZ() >> 4;
            if (editorMinX > editorMaxX) {
                final int lastEditorMinX = editorMinX;
                editorMinX = editorMaxX;
                editorMaxX = lastEditorMinX;
            }
            if (editorMinZ > editorMaxZ) {
                final int lastEditorMinZ = editorMinZ;
                editorMinZ = editorMaxZ;
                editorMaxZ = lastEditorMinZ;
            }
            for (int x2 = editorMinX; x2 <= editorMaxX; ++x2) {
                for (int z2 = editorMinZ; z2 <= editorMaxZ; ++z2) {
                    editorMin.toBukkitWorld().getChunkAt(x2, z2);
                }
            }
        }
        for (final Arena arena : this.plugin.getArenaManager().getArenas().values()) {
            if (!arena.isEnabled()) {
                continue;
            }
            int arenaMinX = arena.getMin().toBukkitLocation().getBlockX() >> 4;
            int arenaMinZ = arena.getMin().toBukkitLocation().getBlockZ() >> 4;
            int arenaMaxX = arena.getMax().toBukkitLocation().getBlockX() >> 4;
            int arenaMaxZ = arena.getMax().toBukkitLocation().getBlockZ() >> 4;
            if (arenaMinX > arenaMaxX) {
                final int lastArenaMinX = arenaMinX;
                arenaMinX = arenaMaxX;
                arenaMaxX = lastArenaMinX;
            }
            if (arenaMinZ > arenaMaxZ) {
                final int lastArenaMinZ = arenaMinZ;
                arenaMinZ = arenaMaxZ;
                arenaMaxZ = lastArenaMinZ;
            }
            for (int x3 = arenaMinX; x3 <= arenaMaxX; ++x3) {
                for (int z3 = arenaMinZ; z3 <= arenaMaxZ; ++z3) {
                    arena.getMin().toBukkitWorld().getChunkAt(x3, z3);
                }
            }
            for (final StandaloneArena saArena : arena.getStandaloneArenas()) {
                arenaMinX = saArena.getMin().toBukkitLocation().getBlockX() >> 4;
                arenaMinZ = saArena.getMin().toBukkitLocation().getBlockZ() >> 4;
                arenaMaxX = saArena.getMax().toBukkitLocation().getBlockX() >> 4;
                arenaMaxZ = saArena.getMax().toBukkitLocation().getBlockZ() >> 4;
                if (arenaMinX > arenaMaxX) {
                    final int lastArenaMinX2 = arenaMinX;
                    arenaMinX = arenaMaxX;
                    arenaMaxX = lastArenaMinX2;
                }
                if (arenaMinZ > arenaMaxZ) {
                    final int lastArenaMinZ2 = arenaMinZ;
                    arenaMinZ = arenaMaxZ;
                    arenaMaxZ = lastArenaMinZ2;
                }
                for (int x4 = arenaMinX; x4 <= arenaMaxX; ++x4) {
                    for (int z4 = arenaMinZ; z4 <= arenaMaxZ; ++z4) {
                        saArena.getMin().toBukkitWorld().getChunkAt(x4, z4);
                    }
                }
            }
        }
        this.plugin.getLogger().info("Finished loading all the chunks!");
        this.chunksLoaded = true;
    }
    
    public boolean isChunksLoaded() {
        return this.chunksLoaded;
    }
}
