package me.devkevin.practice.events.tntrun;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TntRunEvent extends PracticeEvent<TntRunPlayer> {

    private final Map<UUID, TntRunPlayer> players = new HashMap<>();

    private final TntRunCountdownTask countdownTask = new TntRunCountdownTask(this);

    private VoidCheckTask voidCheckTask;
    private RemoveBlocksTask removeBlocksTask;

    @Getter
    private final Map<Location, Material> removeBlocks = Maps.newConcurrentMap();

    public TntRunEvent() {
        super("TNTRun");
    }

    @Override
    public Map<UUID, TntRunPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getCustomLocationManager().getTntLocation());
    }

    public void addBlock(Location location,Block block){
        this.removeBlocks.put(location, block.getType());
    }

    @Override
    public void onStart() {
        new TNTRunGameTask().runTaskTimer(Practice.getInstance(), 0, 20L);
        voidCheckTask = new VoidCheckTask();
        voidCheckTask.runTaskTimer(Practice.getInstance(), 0, 5L);
        removeBlocksTask = new RemoveBlocksTask();
        removeBlocksTask.runTaskTimer(Practice.getInstance(), 0, 1L);
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new TntRunPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {

        return player -> {

            TntRunPlayer data = getPlayer(player);

            if(data.getState() == TntRunPlayer.TntRunState.IN_GAME){
                data.setState(TntRunPlayer.TntRunState.ELIMINATED);

                Profile deathData = Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId());
                deathData.setTntrunEventLosses(deathData.getTntrunEventLosses() + 1);
                PlayerUtil.reset(player);
                this.getPlugin().getProfileManager().giveSpawnItems(player);
                Practice.getInstance().getEventManager().addDeathSpectatorTntRun(player,
                        Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId()), this);


                sendMessage(CC.GOLD + CC.BOLD + "(TnTRun) " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GREEN + " has been eliminated");
                player.sendMessage("");
                player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
                if (!player.hasPermission("practice.donors.gold")) {
                    player.sendMessage(CC.GRAY + "Purchase a rank at https://udrop.buycraft.net/ to host events of your own.");
                }
                player.sendMessage("");
            }

            if (this.getByState(TntRunPlayer.TntRunState.IN_GAME).size() == 1) {
                Player winner = Bukkit.getPlayer(this.getByState(TntRunPlayer.TntRunState.IN_GAME).get(0));

                Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(winner.getUniqueId());
                winnerData.setTntrunEventWins(winnerData.getTntrunEventWins() + 1);

                handleWin(winner);

                voidCheckTask.cancel();
                removeBlocksTask.cancel();

                end();

                this.removeBlocks.forEach((location, block) -> {
                    new BukkitRunnable(){
                        @Override
                        public void run() {
                            location.getBlock().setType(block);
                            location.getBlock().getState().update();
                        }
                    }.runTaskLater(Practice.getInstance(), 10L);
                });
            }
        };
    }

    public List<UUID> getByState(TntRunPlayer.TntRunState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(TntRunPlayer::getUuid).collect(Collectors.toList());
    }

    private static double PLAYER_BOUNDINGBOX_ADD = 0.5;

    private static Block getBlockUnderPlayer(Location location) {
        PlayerPosition loc = new PlayerPosition(location);
        Block b11 = loc.getBlock(location.getWorld(), +PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
        if (b11.getType() != Material.AIR) {
            return b11;
        }
        Block b12 = loc.getBlock(location.getWorld(), -PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
        if (b12.getType() != Material.AIR) {
            return b12;
        }
        Block b21 = loc.getBlock(location.getWorld(), +PLAYER_BOUNDINGBOX_ADD, +PLAYER_BOUNDINGBOX_ADD);
        if (b21.getType() != Material.AIR) {
            return b21;
        }
        Block b22 = loc.getBlock(location.getWorld(), -PLAYER_BOUNDINGBOX_ADD, -PLAYER_BOUNDINGBOX_ADD);
        if (b22.getType() != Material.AIR) {
            return b22;
        }
        return null;
    }

    /**
     * To ensure that the fight doesn't go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class TNTRunGameTask extends BukkitRunnable {

        private int time = 3;

        @Override
        public void run() {

            if (time == 3) {
                PlayerUtil.sendMessage(CC.D_RED + "3...", getBukkitPlayers());
            } else if (time == 2) {
                PlayerUtil.sendMessage(CC.RED + "2...", getBukkitPlayers());
            } else if (time == 1) {
                PlayerUtil.sendMessage(CC.YELLOW + "1...", getBukkitPlayers());
            } else if (time == 0) {
                PlayerUtil.sendMessage(CC.GREEN + "Go!", getBukkitPlayers());

                for (TntRunPlayer player : getPlayers().values()) {
                    player.setState(TntRunPlayer.TntRunState.IN_GAME);
                    this.cancel();
                }

                getBukkitPlayers().forEach(player -> {
                    Location location = player.getLocation().subtract(0, 1, 0);
                    if (location.getBlock().getType() != Material.AIR){
                        addBlock(location, location.getBlock());

                        location.getBlock().setType(Material.AIR);
                        location.getBlock().getState().update();

                        player.sendBlockChange(location.getBlock().getLocation(), Material.AIR, (byte)0);
                    }
                });
            }
            time--;
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class VoidCheckTask extends BukkitRunnable {
        @Override
        public void run() {

            if (getPlayers().size() <= 1) {
                return;
            }

            getBukkitPlayers().forEach(player -> {
                if (getPlayer(player) != null && getPlayer(player).getState() != TntRunPlayer.TntRunState.IN_GAME) {
                    return;
                }
                if (player.getLocation().getY() <= 0) {
                    onDeath().accept(player);
                }
            });
        }
    }

    @Getter
    @RequiredArgsConstructor
    public class RemoveBlocksTask extends BukkitRunnable {
        @Override
        public void run() {

            getByState(TntRunPlayer.TntRunState.IN_GAME).forEach(uuid -> {
                Player player = Bukkit.getPlayer(uuid);

                Location location = player.getLocation().subtract(0, 1, 0);

                if (location.getBlock().getType() != Material.AIR) {
                    Block blocktoremove1 = getBlockUnderPlayer(location);
                    if (blocktoremove1 != null) {
                        addBlock(location, blocktoremove1);

                        blocktoremove1.setType(Material.AIR);
                        blocktoremove1.getState().update();
                        player.sendBlockChange(blocktoremove1.getLocation(), Material.AIR, (byte)0);
                    }
                }
            });
        }
    }

    private static class PlayerPosition {

        private double x;
        private int y;
        private double z;

        @SuppressWarnings("unused")
        public PlayerPosition(double x, int y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public PlayerPosition(Location location) {
            this.x = location.getX();
            this.y = (int) location.getY();
            this.z = location.getZ();
        }

        public Block getBlock(World world, double addx, double addz) {
            return world.getBlockAt(NumberConversions.floor(x + addx), y, NumberConversions.floor(z + addz));
        }
    }
}
