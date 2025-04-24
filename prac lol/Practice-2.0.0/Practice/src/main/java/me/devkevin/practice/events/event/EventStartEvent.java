package me.devkevin.practice.events.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devkevin.practice.events.PracticeEvent;

/**
 * Copyright 22/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@RequiredArgsConstructor
public class EventStartEvent extends BaseEvent {
    private final PracticeEvent event;
}

