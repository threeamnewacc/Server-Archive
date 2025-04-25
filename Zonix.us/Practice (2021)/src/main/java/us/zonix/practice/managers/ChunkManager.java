package us.zonix.practice.managers;

import org.bukkit.Chunk;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.arena.StandaloneArena;

public class ChunkManager {
    private final Practice plugin = Practice.getInstance();
    private boolean chunksLoaded;

    public ChunkManager() {
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, this::loadChunks, 1L);
    }

    private void loadChunks() {
        this.plugin.getLogger().info("Started loading all the chunks...");
        CustomLocation spawnMin = this.plugin.getSpawnManager().getSpawnMin();
        CustomLocation spawnMax = this.plugin.getSpawnManager().getSpawnMax();
        if (spawnMin != null && spawnMax != null) {
            int spawnMinX = spawnMin.toBukkitLocation().getBlockX() >> 4;
            int spawnMinZ = spawnMin.toBukkitLocation().getBlockZ() >> 4;
            int spawnMaxX = spawnMax.toBukkitLocation().getBlockX() >> 4;
            int spawnMaxZ = spawnMax.toBukkitLocation().getBlockZ() >> 4;
            if (spawnMinX > spawnMaxX) {
                int lastSpawnMinX = spawnMinX;
                spawnMinX = spawnMaxX;
                spawnMaxX = lastSpawnMinX;
            }

            if (spawnMinZ > spawnMaxZ) {
                int lastSpawnMinZ = spawnMinZ;
                spawnMinZ = spawnMaxZ;
                spawnMaxZ = lastSpawnMinZ;
            }

            for (int x = spawnMinX; x <= spawnMaxX; x++) {
                for (int z = spawnMinZ; z <= spawnMaxZ; z++) {
                    Chunk chunk = spawnMin.toBukkitWorld().getChunkAt(x, z);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
            }
        }

        CustomLocation editorMin = this.plugin.getSpawnManager().getEditorMin();
        CustomLocation editorMax = this.plugin.getSpawnManager().getEditorMax();
        if (editorMin != null && editorMax != null) {
            int editorMinX = editorMin.toBukkitLocation().getBlockX() >> 4;
            int editorMinZ = editorMin.toBukkitLocation().getBlockZ() >> 4;
            int editorMaxX = editorMax.toBukkitLocation().getBlockX() >> 4;
            int editorMaxZ = editorMax.toBukkitLocation().getBlockZ() >> 4;
            if (editorMinX > editorMaxX) {
                int lastEditorMinX = editorMinX;
                editorMinX = editorMaxX;
                editorMaxX = lastEditorMinX;
            }

            if (editorMinZ > editorMaxZ) {
                int lastEditorMinZ = editorMinZ;
                editorMinZ = editorMaxZ;
                editorMaxZ = lastEditorMinZ;
            }

            for (int x = editorMinX; x <= editorMaxX; x++) {
                for (int zx = editorMinZ; zx <= editorMaxZ; zx++) {
                    Chunk chunk = editorMin.toBukkitWorld().getChunkAt(x, zx);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
            }
        }

        CustomLocation ffaMin = this.plugin.getSpawnManager().getFfaMin();
        CustomLocation ffaMax = this.plugin.getSpawnManager().getFfaMax();
        if (ffaMin != null && ffaMax != null) {
            int ffaMinX = ffaMin.toBukkitLocation().getBlockX() >> 4;
            int ffaMinZ = ffaMin.toBukkitLocation().getBlockZ() >> 4;
            int ffaMaxX = ffaMax.toBukkitLocation().getBlockX() >> 4;
            int ffaMaxZ = ffaMax.toBukkitLocation().getBlockZ() >> 4;
            if (ffaMinX > ffaMaxX) {
                int lastFfaMinX = ffaMinX;
                ffaMinX = ffaMaxX;
                ffaMaxX = lastFfaMinX;
            }

            if (ffaMinZ > ffaMaxZ) {
                int lastFfaMinZ = ffaMinZ;
                ffaMinZ = ffaMaxZ;
                ffaMaxZ = lastFfaMinZ;
            }

            for (int x = ffaMinX; x <= ffaMaxX; x++) {
                for (int zxx = ffaMinZ; zxx <= ffaMaxZ; zxx++) {
                    Chunk chunk = ffaMin.toBukkitWorld().getChunkAt(x, zxx);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
            }
        }

        CustomLocation sumoMin = this.plugin.getSpawnManager().getSumoMin();
        CustomLocation sumoMax = this.plugin.getSpawnManager().getSumoMax();
        if (sumoMin != null && sumoMax != null) {
            int sumoMinX = sumoMin.toBukkitLocation().getBlockX() >> 4;
            int sumoMinZ = sumoMin.toBukkitLocation().getBlockZ() >> 4;
            int sumoMaxX = sumoMax.toBukkitLocation().getBlockX() >> 4;
            int sumoMaxZ = sumoMax.toBukkitLocation().getBlockZ() >> 4;
            if (sumoMinX > sumoMaxX) {
                int lastSumoMinX = sumoMinX;
                sumoMinX = sumoMaxX;
                sumoMaxX = lastSumoMinX;
            }

            if (sumoMinZ > sumoMaxZ) {
                int lastSumoMaxZ = sumoMinZ;
                sumoMinZ = sumoMaxZ;
                sumoMaxZ = lastSumoMaxZ;
            }

            for (int x = sumoMinX; x <= sumoMaxX; x++) {
                for (int zxxx = sumoMinZ; zxxx <= sumoMaxZ; zxxx++) {
                    Chunk chunk = sumoMin.toBukkitWorld().getChunkAt(x, zxxx);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
            }
        }

        CustomLocation oitcMin = this.plugin.getSpawnManager().getOitcMin();
        CustomLocation oitcMax = this.plugin.getSpawnManager().getOitcMax();
        if (oitcMin != null && oitcMax != null) {
            int oitcMinX = oitcMin.toBukkitLocation().getBlockX() >> 4;
            int oitcMinZ = oitcMin.toBukkitLocation().getBlockZ() >> 4;
            int oitcMaxX = oitcMax.toBukkitLocation().getBlockX() >> 4;
            int oitcMaxZ = oitcMax.toBukkitLocation().getBlockZ() >> 4;
            if (oitcMinX > oitcMaxX) {
                int lastOitcMinX = oitcMinX;
                oitcMinX = oitcMaxX;
                oitcMaxX = lastOitcMinX;
            }

            if (oitcMinZ > oitcMaxZ) {
                int lastOitcMaxZ = oitcMinZ;
                oitcMinZ = oitcMaxZ;
                oitcMaxZ = lastOitcMaxZ;
            }

            for (int x = oitcMinX; x <= oitcMaxX; x++) {
                for (int zxxxx = oitcMinZ; zxxxx <= oitcMaxZ; zxxxx++) {
                    Chunk chunk = oitcMin.toBukkitWorld().getChunkAt(x, zxxxx);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
            }
        }

        CustomLocation parkourMin = this.plugin.getSpawnManager().getParkourMin();
        CustomLocation parkourMax = this.plugin.getSpawnManager().getParkourMax();
        if (parkourMin != null && parkourMax != null) {
            int parkourMinX = parkourMin.toBukkitLocation().getBlockX() >> 4;
            int parkourMinZ = parkourMin.toBukkitLocation().getBlockZ() >> 4;
            int parkourMaxX = parkourMax.toBukkitLocation().getBlockX() >> 4;
            int parkourMaxZ = parkourMax.toBukkitLocation().getBlockZ() >> 4;
            if (parkourMinX > parkourMaxX) {
                int lastParkourMinX = parkourMinX;
                parkourMinX = parkourMaxX;
                parkourMaxX = lastParkourMinX;
            }

            if (parkourMinZ > parkourMaxZ) {
                int lastParkourMaxZ = parkourMinZ;
                parkourMinZ = parkourMaxZ;
                parkourMaxZ = lastParkourMaxZ;
            }

            for (int x = parkourMinX; x <= parkourMaxX; x++) {
                for (int zxxxxx = parkourMinZ; zxxxxx <= parkourMaxZ; zxxxxx++) {
                    Chunk chunk = parkourMin.toBukkitWorld().getChunkAt(x, zxxxxx);
                    if (!chunk.isLoaded()) {
                        chunk.load();
                    }
                }
            }
        }

        for (Arena arena : this.plugin.getArenaManager().getArenas().values()) {
            if (arena.isEnabled()) {
                int arenaMinX = arena.getMin().toBukkitLocation().getBlockX() >> 4;
                int arenaMinZ = arena.getMin().toBukkitLocation().getBlockZ() >> 4;
                int arenaMaxX = arena.getMax().toBukkitLocation().getBlockX() >> 4;
                int arenaMaxZ = arena.getMax().toBukkitLocation().getBlockZ() >> 4;
                if (arenaMinX > arenaMaxX) {
                    int lastArenaMinX = arenaMinX;
                    arenaMinX = arenaMaxX;
                    arenaMaxX = lastArenaMinX;
                }

                if (arenaMinZ > arenaMaxZ) {
                    int lastArenaMinZ = arenaMinZ;
                    arenaMinZ = arenaMaxZ;
                    arenaMaxZ = lastArenaMinZ;
                }

                for (int x = arenaMinX; x <= arenaMaxX; x++) {
                    for (int zxxxxxx = arenaMinZ; zxxxxxx <= arenaMaxZ; zxxxxxx++) {
                        Chunk chunk = arena.getMin().toBukkitWorld().getChunkAt(x, zxxxxxx);
                        if (!chunk.isLoaded()) {
                            chunk.load();
                        }
                    }
                }

                for (StandaloneArena saArena : arena.getStandaloneArenas()) {
                    arenaMinX = saArena.getMin().toBukkitLocation().getBlockX() >> 4;
                    arenaMinZ = saArena.getMin().toBukkitLocation().getBlockZ() >> 4;
                    arenaMaxX = saArena.getMax().toBukkitLocation().getBlockX() >> 4;
                    arenaMaxZ = saArena.getMax().toBukkitLocation().getBlockZ() >> 4;
                    if (arenaMinX > arenaMaxX) {
                        int lastArenaMinX = arenaMinX;
                        arenaMinX = arenaMaxX;
                        arenaMaxX = lastArenaMinX;
                    }

                    if (arenaMinZ > arenaMaxZ) {
                        int lastArenaMinZ = arenaMinZ;
                        arenaMinZ = arenaMaxZ;
                        arenaMaxZ = lastArenaMinZ;
                    }

                    for (int x = arenaMinX; x <= arenaMaxX; x++) {
                        for (int zxxxxxxx = arenaMinZ; zxxxxxxx <= arenaMaxZ; zxxxxxxx++) {
                            Chunk chunk = saArena.getMin().toBukkitWorld().getChunkAt(x, zxxxxxxx);
                            if (!chunk.isLoaded()) {
                                chunk.load();
                            }
                        }
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
