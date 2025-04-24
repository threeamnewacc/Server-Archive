package me.devkevin.practice.arena.chunk.restoration;

import me.devkevin.practice.arena.cuboid.Cuboid;
import me.devkevin.practice.arena.standalone.StandaloneArena;

/**
 * Copyright 19/10/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public interface IChunkRestoration {
    void copy(StandaloneArena arena);
    void reset(StandaloneArena arena);
    void copy(Cuboid cuboid);
    void reset(Cuboid cuboid);
}
