package me.devkevin.practice.profile.state;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public enum ProfileState {

    LOADING,
    SPAWN,
    QUEUE,
    EVENT,
    FIGHTING,
    PARTY,
    SPECTATING,
    EDITING,
    STAFF,
    FACTION,
    BOT_FIGHTING,

    // Match system (25/04/2021)
    SoloUnranked,
    RankedSolo
}