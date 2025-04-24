package me.devkevin.practice.match.event.impl;

import lombok.Getter;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.match.event.MatchEvent;

/**
 * Copyright 28/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class MatchEndEvent extends MatchEvent {

    private final MatchTeam winningTeam;
    private final MatchTeam losingTeam;

    public MatchEndEvent(Match match, MatchTeam winningTeam, MatchTeam losingTeam) {
        super(match);
        this.winningTeam = winningTeam;
        this.losingTeam = losingTeam;
    }
}