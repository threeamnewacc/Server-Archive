package me.devkevin.practice.events.tntrun;

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
public class TntRunPlayer extends EventPlayer {

    private TntRunState state = TntRunState.WAITING;
    private BukkitTask fightTask;
    private TntRunPlayer fighting;

    public TntRunPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public enum TntRunState {
        WAITING,
        IN_GAME,
        ELIMINATED
    }
}
