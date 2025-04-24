package me.devkevin.practice.util;

import me.devkevin.practice.Practice;

/**
 * Copyright 15/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TaskUtil {

    public static void run(Runnable runnable) {
        Practice.getInstance().getServer().getScheduler().runTask(Practice.getInstance(), runnable);
    }

    public static void runTimer(Runnable runnable, long delay, long timer) {
        Practice.getInstance().getServer().getScheduler().runTaskTimer(Practice.getInstance(), runnable, delay, timer);
    }

    public static void runLater(Runnable runnable, long delay) {
        Practice.getInstance().getServer().getScheduler().runTaskLater(Practice.getInstance(), runnable, delay);
    }

    public static void runAsync(Runnable runnable) {
        Practice.getInstance().getServer().getScheduler().runTaskAsynchronously(Practice.getInstance(), runnable);
    }
}
