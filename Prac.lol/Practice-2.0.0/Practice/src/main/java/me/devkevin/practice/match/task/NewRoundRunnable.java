package me.devkevin.practice.match.task;

import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright 18/12/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class NewRoundRunnable extends BukkitRunnable {
    private final Match match;
    private final Player player;
    private int ticks;

    public NewRoundRunnable(Match match, Player player) {
        this.match = match;
        this.player = player;
        this.ticks = 4;

        this.runTaskTimer(Practice.getInstance(), 20L , 20L);
    }

    @Override
    public void run() {
        this.ticks--;

        if (this.player == null) {
            this.cancel();
            return;
        }

        switch (this.ticks) {
            case 3:
                this.player.setAllowFlight(true);
                this.player.setFlying(true);
                this.player.setMetadata("e", new FixedMetadataValue(Practice.getInstance(), ""));
                break;
            case 2:
            case 1:
                this.player.sendMessage(CC.YELLOW + "You will respawn in " + CC.PINK + this.ticks + CC.YELLOW + " second" + (this.ticks == 1 ? "" : "s" + "."));
                break;
            case 0:
                //TODO:
                break;
        }
    }
}
