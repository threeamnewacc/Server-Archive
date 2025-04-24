package me.devkevin.practice.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Copyright 12/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@Setter
@RequiredArgsConstructor
public class QueueEntry {

    private final QueueType queueType;
    private final String kitName;

    private final int elo;

    private final boolean party;

    private boolean found = false; // prevent player joining multiple games with 1 queue.
}
