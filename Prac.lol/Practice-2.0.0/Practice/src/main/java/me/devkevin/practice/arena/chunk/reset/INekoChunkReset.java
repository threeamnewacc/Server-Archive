package me.devkevin.practice.arena.chunk.reset;

import net.minecraft.server.v1_8_R3.*;

/**
 * Copyright 19/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public interface INekoChunkReset {
    void setSections(Chunk nmsChunk, ChunkSection[] sections);
    ChunkSection[] cloneSections(ChunkSection[] sections);
    ChunkSection cloneSection(ChunkSection section);

    default NibbleArray cloneNibbleArray(NibbleArray nibbleArray) {
        return new NibbleArray(nibbleArray.a().clone());
    }
}
