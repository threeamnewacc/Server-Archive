package me.devkevin.practice.arena.chunk.manager;

import lombok.Getter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.location.CustomLocation;
import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright 19/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ChunkManager {
    private final Practice plugin = Practice.getInstance();

    @Getter private boolean chunksLoaded;

    public ChunkManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                loadChunks();
            }
        }.runTaskLater(this.plugin, 5L);
    }

    private void loadChunks() {
        plugin.getLogger().info("Starting chunk loading task!");

        CustomLocation spawnMin = plugin.getCustomLocationManager().getSpawnMin();
        CustomLocation spawnMax = plugin.getCustomLocationManager().getSpawnMax();

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
        } else {
            plugin.getLogger().info(" ");
            plugin.getLogger().info("                ERROR     ERROR    ERROR                ");
            plugin.getLogger().info("Please make sure you set the Spawn Min & Max Locations!");
            plugin.getLogger().info("If you did not, remove 'spawnLocation' from settings.yml");
            plugin.getLogger().info("                ERROR     ERROR    ERROR                ");
            plugin.getLogger().info(" ");
        }

        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            if (!arena.isEnabled()) {
                continue;
            }
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
                for (int z = arenaMinZ; z <= arenaMaxZ; z++) {
                    Chunk chunk = arena.getMin().toBukkitWorld().getChunkAt(x, z);
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
                    for (int z = arenaMinZ; z <= arenaMaxZ; z++) {
                        Chunk chunk = saArena.getMin().toBukkitWorld().getChunkAt(x, z);
                        if (!chunk.isLoaded()) {
                            chunk.load();
                        }
                    }
                }
            }
        }

        plugin.getLogger().info("Finished loading all the chunks!");
        this.chunksLoaded = true;
    }
}
