package me.devkevin.practice.arena.chunk.manager;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.arena.chunk.data.NekoChunkData;
import me.devkevin.practice.arena.chunk.reset.INekoChunkReset;
import me.devkevin.practice.arena.chunk.reset.impl.VanillaNekoChunkReset;
import me.devkevin.practice.arena.chunk.restoration.IChunkRestoration;
import me.devkevin.practice.arena.chunk.restoration.impl.PracticeChunkRestoration;
import me.devkevin.practice.arena.cuboid.Cuboid;
import me.devkevin.practice.arena.standalone.StandaloneArena;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright 19/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class ChunkRestorationManager {
    @Getter @Setter private static INekoChunkReset iNekoChunkReset;
    @Getter @Setter private static IChunkRestoration iChunkRestoration;

    private final Map<StandaloneArena, NekoChunkData> chunks = new ConcurrentHashMap<>();
    private final Map<Cuboid, NekoChunkData> eventMapChunks = new ConcurrentHashMap<>();

    public ChunkRestorationManager() {
        if (iNekoChunkReset == null) { // Let the other plugins create an INekoReset before we load ours.
            iNekoChunkReset = new VanillaNekoChunkReset();
        }
        if (iChunkRestoration == null) { // Let the other plugins create an IChunkRestoration before we load ours.
            iChunkRestoration = new PracticeChunkRestoration(iNekoChunkReset, this);
        }
    }
}
