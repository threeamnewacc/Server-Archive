package me.devkevin.practice.profile.task;

import me.devkevin.practice.Practice;
import me.devkevin.practice.match.timer.impl.EnderpearlTimer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Copyright 15/08/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class ExpBarTask implements Runnable {

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        EnderpearlTimer timer = Practice.getInstance().getTimerManager().getTimer(EnderpearlTimer.class);

        for (UUID uuid : timer.getCooldowns().keySet()) {
            Player player = Practice.getInstance().getServer().getPlayer(uuid);

            if (player != null) {
                long time = timer.getRemaining(player);
                int seconds = (int) Math.round((double) time / 1000.0D);

                player.setLevel(seconds);
                player.setExp((float) time / 15000.0F);
            }
        }
    }
}