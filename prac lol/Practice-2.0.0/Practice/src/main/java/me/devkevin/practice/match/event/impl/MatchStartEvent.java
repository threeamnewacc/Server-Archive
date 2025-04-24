package me.devkevin.practice.match.event.impl;

import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.event.MatchEvent;

/**
 * Copyright 28/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class MatchStartEvent extends MatchEvent {

    public MatchStartEvent(Match match) {
        super(match);
    }
}
