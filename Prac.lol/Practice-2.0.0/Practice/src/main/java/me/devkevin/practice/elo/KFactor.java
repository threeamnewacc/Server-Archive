package me.devkevin.practice.elo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Copyright 05/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@RequiredArgsConstructor
public class KFactor {
    private final int startIndex;
    private final int endIndex;
    private final double value;
}

