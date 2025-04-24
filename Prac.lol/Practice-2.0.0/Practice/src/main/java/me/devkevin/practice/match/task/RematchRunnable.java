package me.devkevin.practice.match.task;

import lombok.RequiredArgsConstructor;
import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Copyright 01/12/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@RequiredArgsConstructor
public class RematchRunnable implements Runnable {
    private final Practice plugin = Practice.getInstance();

    private final UUID playerUUID;

    @Override
    public void run() {
        Player player = this.plugin.getServer().getPlayer(this.playerUUID);

        if (player != null) {
            Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

            if (profile != null) {
                if (profile.isInSpawn()) {
                    profile.setRematchID(-1);
                }
            }
            this.plugin.getMatchManager().removeRematch(playerUUID);
        }
    }
}
