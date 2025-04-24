package me.devkevin.practice.util;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 20/10/2019 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class Circle {

    public static List<Location> getCircle(Location center, float radius, int amount) {
        List<Location> list = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            double a = 2 * Math.PI / amount * i;
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            list.add(center.clone().add(x, 0, z));
        }
        return list;
    }
}
