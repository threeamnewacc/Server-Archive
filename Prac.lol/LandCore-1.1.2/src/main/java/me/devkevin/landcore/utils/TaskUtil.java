package me.devkevin.landcore.utils;

import me.devkevin.landcore.LandCore;

public class TaskUtil {

    public static void run(Runnable runnable) {
        LandCore.getInstance().getServer().getScheduler().runTask(LandCore.getInstance(), runnable);
    }

    public static void runLater(Runnable runnable, long delay) {
        LandCore.getInstance().getServer().getScheduler().runTaskLater(LandCore.getInstance(), runnable, delay);
    }

    public static void runAsync(Runnable runnable) {
        LandCore.getInstance().getServer().getScheduler().runTaskAsynchronously(LandCore.getInstance(), runnable);
    }
}
