package me.devkevin.practice.leaderboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.kit.Kit;

import java.util.UUID;

/**
 * Copyright 13/05/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter @Setter @AllArgsConstructor
public class Leaderboard {
    private int elo;
    private int winStreak;
    private UUID uuid;
    private String name;
    private Kit kit;
}
