package me.devkevin.practice.profile.task;

import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Copyright 19/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class SpawnPlayerVisibilityRunnable extends BukkitRunnable {
    private final Practice plugin = Practice.getInstance();

    private final static boolean SHOW_PLAYERS = true; //# This will override the player-visibility on spawn and show all the online players in the server.
    
    @Override
    public void run() {

        if (SHOW_PLAYERS) {
            this.plugin.getServer().getOnlinePlayers().forEach(player -> {
                Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

                if (profile == null) {
                    return;
                }
                if (profile.isInSpawn() || profile.isInQueue()) {
                    this.plugin.getServer().getOnlinePlayers().forEach(op -> this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                        if (op == null) {
                            return;
                        }

                        Profile onlineData = this.plugin.getProfileManager().getProfileData(op.getUniqueId());
                        if (onlineData == null) {
                            return;
                        }

                        if (onlineData.isInSpawn() || profile.isInQueue()) {
                            player.showPlayer(op);
                            op.showPlayer(player);
                        }
                    }));
                }
            });
        } else {
            this.plugin.getServer().getOnlinePlayers().forEach(player -> {
                Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

                if (profile.isInSpawn()) {
                    if (profile.getOptions().isVisibility()) {
                        this.plugin.getServer().getOnlinePlayers().forEach(op -> this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                            if (op == null) {
                                return;
                            }

                            Profile onlineData = this.plugin.getProfileManager().getProfileData(op.getUniqueId());
                            if (onlineData == null) {
                                return;
                            }

                            if (onlineData.getState() == ProfileState.SPAWN && op.hasPermission("practice.user.spawn-visibility")) {
                                player.showPlayer(op);
                            } else {
                                player.hidePlayer(op);
                            }
                        }));
                    } else {
                        this.plugin.getServer().getOnlinePlayers().forEach(op -> this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                            if (op == null) {
                                return;
                            }

                            Profile onlineData = this.plugin.getProfileManager().getProfileData(op.getUniqueId());
                            if (onlineData == null) {
                                return;
                            }

                            if (onlineData.getState() == ProfileState.SPAWN) {
                                player.hidePlayer(op);
                            }
                        }));
                    }
                }
            });
        }
    }
}
