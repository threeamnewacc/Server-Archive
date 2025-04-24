package me.devkevin.practice.arena.chunk.data;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.ChunkSection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright 19/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter @Setter
public class NekoChunkData {
    public Map<NekoChunk, ChunkSection[]> chunks = new ConcurrentHashMap<>();

    public ChunkSection[] getNyaChunk(int x, int z) {
        for (Map.Entry<NekoChunk, ChunkSection[]> chunksFromMap : chunks.entrySet()) {
            if (chunksFromMap.getKey().getX() == x && chunksFromMap.getKey().getZ() == z) {
                return chunksFromMap.getValue();
            }
        }
        return null;
    }
}
