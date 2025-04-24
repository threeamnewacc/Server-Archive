package me.devkevin.practice.events.tnttag;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.events.EventPlayer;
import me.devkevin.practice.events.PracticeEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@Setter
public class TNTTagPlayer extends EventPlayer {

    private TNTTagPlayer.TNTTagState state = TNTTagPlayer.TNTTagState.WAITING;
    private BukkitTask fightTask;
    private TNTTagPlayer fighting;

    public TNTTagPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum TNTTagState {
        WAITING,
        IN_GAME,
        ELIMINATED,
        TAGGED
    }
}
