package me.devkevin.practice.match.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.devkevin.practice.match.Match;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Copyright 28/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@AllArgsConstructor
public class MatchEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Match match;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
