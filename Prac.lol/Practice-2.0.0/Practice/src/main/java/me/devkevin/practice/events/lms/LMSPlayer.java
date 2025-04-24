package me.devkevin.practice.events.lms;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;

import java.util.UUID;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Setter
@Getter
public class LMSPlayer extends EventPlayer {

    private LMSState state = LMSState.WAITING;

    public LMSPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum LMSState {
        WAITING,
        FIGHTING,
        ELIMINATED
    }
}

