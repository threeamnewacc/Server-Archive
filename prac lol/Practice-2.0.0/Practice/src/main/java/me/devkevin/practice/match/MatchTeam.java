package me.devkevin.practice.match;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.team.KillableTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Copyright 28/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class MatchTeam extends KillableTeam {

    private final int teamID;
    private int bridgesPoints;
    @Setter private int lives;
    @Setter private Location bridgeSpawnLocation;

    private boolean hasBed = true;
    private boolean ableToScore = true;
    @Setter int matchWins = 0;

    /**
     * All players who are currently alive.
     */
    private final Set<UUID> aliveMembers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public MatchTeam(UUID leader, List<UUID> players, int teamID) {
        super(leader, players);
        this.teamID = teamID;
    }


    public void addPoint() {
        this.ableToScore = false;
        bridgesPoints = bridgesPoints + 1;
        Bukkit.getServer().getScheduler().runTaskLater(Practice.getInstance(), () -> this.ableToScore = true, 100L);
    }

    public void destroyBed() {
        this.hasBed = false;
    }

    public void removeLife() {
        lives = lives - 1;
    }
}
