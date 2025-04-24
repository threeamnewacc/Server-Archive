package me.devkevin.practice.events.lms;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class LMSEvent extends PracticeEvent<LMSPlayer> {

    private final Map<UUID, LMSPlayer> players = new HashMap<>();
    private final LMSCountdownTask countdownTask = new LMSCountdownTask(this);
    private LMSEvent.LMSGameTask gameTask;

    public LMSEvent() {
        super("LMS");
    }

    @Override
    public Map<UUID, LMSPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return getPlugin().getCustomLocationManager().getLmsLocations();
    }

    @Override
    public void onStart() {
        gameTask = new LMSGameTask();
        gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20L);
    }

    public void cancelAll() {
        if(gameTask != null) {
            gameTask.cancel();
        }
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new LMSPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            LMSPlayer data = getPlayer(player);

            if(data.getState() != LMSPlayer.LMSState.FIGHTING) {
                return;
            }

            Player killer = player.getKiller();

            data.setState(LMSPlayer.LMSState.ELIMINATED);

            getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                getPlugin().getProfileManager().sendToSpawn(player);
                if(getPlayers().size() >= 2) {
                    getPlugin().getEventManager().addSpectatorLMS(player, getPlugin().getProfileManager().getProfileData(player.getUniqueId()), this);
                }
            });

            getPlayers().remove(player.getUniqueId());

            sendMessage(CC.GOLD + CC.BOLD + "(LMS) " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GREEN + " has been eliminated");
            player.sendMessage("");
            player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
            if (!player.hasPermission("practice.donors.gold")) {
                player.sendMessage(CC.GRAY + "Purchase a rank at https://udrop.buycraft.net/ to host events of your own.");
            }
            player.sendMessage("");

            if(getByState(LMSPlayer.LMSState.FIGHTING).size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(LMSPlayer.LMSState.FIGHTING).get(0));
                if(winner != null) {
                    handleWin(winner);
                }

                end();
                cancelAll();
            }
        };
    }

    private Player getRandomPlayer() {
        if(getByState(LMSPlayer.LMSState.FIGHTING).size() == 0) {
            return null;
        }

        List<UUID> fighting = getByState(LMSPlayer.LMSState.FIGHTING);

        Collections.shuffle(fighting);

        UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

        return getPlugin().getServer().getPlayer(uuid);
    }

    public List<UUID> getByState(LMSPlayer.LMSState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(LMSPlayer::getUuid).collect(Collectors.toList());
    }
    /**
     * To ensure that the fight does not go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class LMSGameTask extends BukkitRunnable {

        private int time = 303;

        @Override
        public void run() {
            if(time == 303) {
                PlayerUtil.sendMessage(CC.D_RED + "3...", getBukkitPlayers());
            } else if(time == 302) {
                PlayerUtil.sendMessage(CC.RED + "2...", getBukkitPlayers());
            } else if(time == 301) {
                PlayerUtil.sendMessage(CC.YELLOW + "1...", getBukkitPlayers());
            } else if(time == 300) {
                PlayerUtil.sendMessage(CC.GREEN + "Go!", getBukkitPlayers());
                getPlayers().values().forEach(player -> player.setState(LMSPlayer.LMSState.FIGHTING));
                getBukkitPlayers().forEach(LMSEvent.this::handleInventory);
            } else if(time <= 0) {
                Player winner = getRandomPlayer();

                if(winner != null) {
                    handleWin(winner);
                }

                end();
                cancel();
                return;
            }

            if(getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(getByState(LMSPlayer.LMSState.FIGHTING).get(0));

                if(winner != null) {
                    handleWin(winner);
                }

                end();
                cancel();
                return;
            }

            if(Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
                sendMessage(CC.GOLD + CC.BOLD + "(LMS) " + CC.GREEN + "Game ends in " + CC.RESET + time + " seconds" + CC.GREEN + ".");
            } else if(Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
                sendMessage(CC.GOLD + CC.BOLD + "(LMS) " + CC.GREEN + "Game is ending in " + CC.RESET + time + " seconds" + CC.GREEN + ".");
            }

            time--;
        }
    }

    private void handleInventory(Player player) {
        //Practice.getInstance().getKitManager().getKit("NoDebuff").applyToPlayer(player);
    }
}

