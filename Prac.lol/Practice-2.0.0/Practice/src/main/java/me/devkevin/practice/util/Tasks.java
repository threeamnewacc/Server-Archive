package me.devkevin.practice.util;

import me.devkevin.practice.Practice;
import org.bukkit.Bukkit;

/**
 * Copyright 19/01/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class Tasks {

    public static void runAsyncTimer(final Callable callable, final long delay, final long interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Practice.getInstance(), callable::call, delay, interval);
    }

    public interface Callable {
        void call();
    }
}

