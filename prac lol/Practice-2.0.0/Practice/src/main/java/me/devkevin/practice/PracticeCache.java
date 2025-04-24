package me.devkevin.practice;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.devkevin.practice.profile.Profile;
import org.bukkit.Bukkit;

/**
 * Copyright 10/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PracticeCache extends Thread {

    @Getter private static PracticeCache instance;

    private int onlinePlayers;
    private int queueingPlayers;
    private int playingPlayers;
    private int onlineStaffModePlayers;

    private int unrankedSoloPlayers;
    private int rankedSoloPlayers;

    public PracticeCache() {
        instance = this;
    }

    @Override
    public void run() {
        while (true) {
            onlinePlayers = Bukkit.getOnlinePlayers().size();
            int queueingPlayers = 0;
            int playingPlayers = 0;
            int onlineStaffModePlayers = 0;
            int unrankedSoloPlayers = 0;
            int rankedSoloPlayers = 0;

            for (Profile profile : Practice.getInstance().getProfileManager().getAllData()) {
                if (profile.isInQueue()) {
                    queueingPlayers++;
                }

                if (profile.isFighting()) {
                    playingPlayers++;
                }

                if (profile.isInStaffMode()) {
                    onlineStaffModePlayers++;
                }

                if (profile.isInSoloUnranked()) {
                    unrankedSoloPlayers++;
                }

                if (profile.isInSoloRanked()) {
                    rankedSoloPlayers++;
                }
            }

            this.queueingPlayers = queueingPlayers;
            this.playingPlayers = playingPlayers;
            this.onlineStaffModePlayers = onlineStaffModePlayers;
            this.unrankedSoloPlayers = unrankedSoloPlayers;
            this.rankedSoloPlayers = rankedSoloPlayers;

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
