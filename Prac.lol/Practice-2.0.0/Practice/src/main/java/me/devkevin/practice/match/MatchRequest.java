package me.devkevin.practice.match;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devkevin.practice.arena.Arena;

import java.util.UUID;

/**
 * Copyright 29/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@RequiredArgsConstructor
public class MatchRequest {

    private final UUID requester;
    private final UUID requested;

    private final Arena arena;
    private final String kitName;
    private final boolean party;
}

