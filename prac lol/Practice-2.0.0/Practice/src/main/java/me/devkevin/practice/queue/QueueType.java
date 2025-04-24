package me.devkevin.practice.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Copyright 11/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@RequiredArgsConstructor
public enum QueueType {

    UN_RANKED("Unranked"),
    RANKED("Ranked"),
    FACTION("Faction");

    private final String name;

    public boolean isRanked() {
        return this == RANKED;
    }

    public boolean isUnranked() {
        return this == UN_RANKED;
    }

    public boolean isFaction() {
        return this == FACTION;
    }
}
