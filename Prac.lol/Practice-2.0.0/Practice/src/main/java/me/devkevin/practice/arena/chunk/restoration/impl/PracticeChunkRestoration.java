package me.devkevin.practice.arena.chunk.restoration.impl;

import lombok.RequiredArgsConstructor;
import me.devkevin.practice.arena.chunk.data.NekoChunk;
import me.devkevin.practice.arena.chunk.data.NekoChunkData;
import me.devkevin.practice.arena.chunk.manager.ChunkRestorationManager;
import me.devkevin.practice.arena.chunk.reset.INekoChunkReset;
import me.devkevin.practice.arena.chunk.restoration.IChunkRestoration;
import me.devkevin.practice.arena.cuboid.Cuboid;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import net.minecraft.server.v1_8_R3.ChunkSection;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;

/**
 * Copyright 19/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@RequiredArgsConstructor
public class PracticeChunkRestoration implements IChunkRestoration {
    private final INekoChunkReset iNekoChunkReset;
    private final ChunkRestorationManager chunkRestorationManager;

    @Override
    public void copy(StandaloneArena arena) {
        Cuboid cuboid = new Cuboid(arena.getMin().toBukkitLocation(), arena.getMax().toBukkitLocation());

        long startTime = System.currentTimeMillis();

        NekoChunkData nekoChunkData = new NekoChunkData();
        cuboid.getChunks().forEach(chunk -> {
            chunk.load();
            net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
            ChunkSection[] nmsSections = iNekoChunkReset.cloneSections(nmsChunk.getSections());
            nekoChunkData.chunks.put(new NekoChunk(chunk.getX(), chunk.getZ()), iNekoChunkReset.cloneSections(nmsSections));
        });
        chunkRestorationManager.getChunks().put(arena, nekoChunkData);
    }

    @Override
    public void reset(StandaloneArena arena) {
        long startTime = System.currentTimeMillis();

        Cuboid cuboid = new Cuboid(arena.getMin().toBukkitLocation(), arena.getMax().toBukkitLocation());
        resetCuboid(cuboid, chunkRestorationManager.getChunks().get(arena));

        System.out.println("Chunks have been reset! (took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    @Override
    public void copy(Cuboid cuboid) {
        long startTime = System.currentTimeMillis();

        NekoChunkData nekoChunkData = new NekoChunkData();
        cuboid.getChunks().forEach(chunk -> {
            chunk.load();
            net.minecraft.server.v1_8_R3.Chunk nmsChunk = ((CraftChunk) chunk).getHandle();
            ChunkSection[] nmsSections = iNekoChunkReset.cloneSections(nmsChunk.getSections());
            nekoChunkData.chunks.put(new NekoChunk(chunk.getX(), chunk.getZ()), iNekoChunkReset.cloneSections(nmsSections));
        });
        chunkRestorationManager.getEventMapChunks().put(cuboid, nekoChunkData);

        System.out.println("Chunks copied for SkyWars Event! (" + (System.currentTimeMillis() - startTime) + "ms)");
    }

    @Override
    public void reset(Cuboid cuboid) {
        long startTime = System.currentTimeMillis();
        resetCuboid(cuboid, chunkRestorationManager.getEventMapChunks().get(cuboid));

        System.out.println("Chunks have been reset for SkyWars Event! (took " + (System.currentTimeMillis() - startTime) + "ms)");
    }

    private void resetCuboid(Cuboid cuboid, NekoChunkData chunkData) {
        cuboid.getChunks().forEach(chunk -> {
            try {
                chunk.load();
                iNekoChunkReset.setSections(((CraftChunk) chunk).getHandle(), iNekoChunkReset.cloneSections(chunkData.getNyaChunk(chunk.getX(), chunk.getZ())));
                chunk.getWorld().refreshChunk(chunk.getX(), chunk.getZ()); // let the mf server know that you've updated the chunk.
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
