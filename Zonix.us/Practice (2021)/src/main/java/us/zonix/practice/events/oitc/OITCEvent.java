package us.zonix.practice.events.oitc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventCountdownTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;

public class OITCEvent extends PracticeEvent<OITCPlayer> {
    private final Map<UUID, OITCPlayer> players = new HashMap<>();
    private OITCEvent.OITCGameTask gameTask = null;
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);
    private List<CustomLocation> respawnLocations;

    public OITCEvent() {
        super("OITC", ItemUtil.createItem(Material.DIAMOND_SWORD, ChatColor.RED + "OITC Event"), false);
    }

    @Override
    public Map<UUID, OITCPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getOitcLocation());
    }

    @Override
    public void onStart() {
        this.respawnLocations = new ArrayList<>();
        this.gameTask = new OITCEvent.OITCGameTask();
        this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            OITCPlayer data = this.getPlayer(player);
            if (data != null) {
                if (data.getState() != OITCPlayer.OITCState.WAITING) {
                    if (data.getState() == OITCPlayer.OITCState.FIGHTING
                        || data.getState() == OITCPlayer.OITCState.PREPARING
                        || data.getState() == OITCPlayer.OITCState.RESPAWNING) {
                        String deathMessage = ChatColor.RED
                            + "[Event] "
                            + ChatColor.RED
                            + player.getName()
                            + "("
                            + data.getScore()
                            + ")"
                            + ChatColor.GRAY
                            + " has been eliminated from the game.";
                        if (data.getLastKiller() != null) {
                            OITCPlayer killerData = data.getLastKiller();
                            Player killer = this.getPlugin().getServer().getPlayer(killerData.getUuid());
                            int count = killerData.getScore() + 1;
                            killerData.setScore(count);
                            killer.getInventory()
                                .setItem(
                                    6,
                                    ItemUtil.createItem(
                                        Material.GLOWSTONE_DUST,
                                        ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills",
                                        killerData.getScore() == 0 ? 1 : killerData.getScore()
                                    )
                                );
                            if (killer.getInventory().contains(Material.ARROW)) {
                                killer.getInventory().getItem(8).setAmount(killer.getInventory().getItem(8).getAmount() + 2);
                            } else {
                                killer.getInventory().setItem(8, new ItemStack(Material.ARROW, 2));
                            }

                            killer.updateInventory();
                            killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 1.0F, 1.0F);
                            FireworkEffect fireworkEffect = FireworkEffect.builder()
                                .withColor(Color.fromRGB(127, 56, 56))
                                .withFade(Color.fromRGB(127, 56, 56))
                                .with(Type.BALL)
                                .build();
                            PlayerUtil.sendFirework(fireworkEffect, player.getLocation().add(0.0, 1.5, 0.0));
                            data.setLastKiller(null);
                            deathMessage = ChatColor.RED
                                + "[Event] "
                                + ChatColor.RED
                                + player.getName()
                                + "("
                                + data.getScore()
                                + ")"
                                + ChatColor.GRAY
                                + " has been killed"
                                + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName() + "(" + count + ")");
                            if (count == 25) {
                                PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(killer.getUniqueId());
                                winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);
                                String announce = ChatColor.DARK_RED
                                    + killer.getName()
                                    + ChatColor.WHITE
                                    + " has won our "
                                    + ChatColor.DARK_RED
                                    + "OITC"
                                    + ChatColor.WHITE
                                    + " event!";
                                Bukkit.broadcastMessage(announce);
                                this.gameTask.cancel();
                                this.end();
                            }
                        }

                        if (data.getLastKiller() == null) {
                            data.setLives(data.getLives() - 1);
                            if (data.getLives() == 0) {
                                this.getPlayers().remove(player.getUniqueId());
                                player.sendMessage(ChatColor.RED + "[Event] " + ChatColor.GRAY + "You have been eliminated from the game.");
                                this.getPlugin()
                                    .getServer()
                                    .getScheduler()
                                    .runTask(
                                        this.getPlugin(),
                                        () -> {
                                            this.getPlugin().getPlayerManager().sendToSpawnAndReset(player);
                                            if (this.getPlayers().size() >= 2) {
                                                this.getPlugin()
                                                    .getEventManager()
                                                    .addSpectatorOITC(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
                                            }
                                        }
                                    );
                            } else {
                                BukkitTask respawnTask = new OITCEvent.RespawnTask(player, data).runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
                                data.setRespawnTask(respawnTask);
                            }
                        }

                        this.sendMessage(new String[]{deathMessage});
                    }
                }
            }
        };
    }

    public void teleportNextLocation(Player player) {
        player.teleport(this.getGameLocations().remove(ThreadLocalRandom.current().nextInt(this.getGameLocations().size())).toBukkitLocation());
    }

    private List<CustomLocation> getGameLocations() {
        if (this.respawnLocations != null && this.respawnLocations.size() == 0) {
            this.respawnLocations.addAll(this.getPlugin().getSpawnManager().getOitcSpawnpoints());
        }

        return this.respawnLocations;
    }

    private void giveRespawnItems(Player player, OITCPlayer oitcPlayer) {
        this.getPlugin()
            .getServer()
            .getScheduler()
            .runTask(
                this.getPlugin(),
                () -> {
                    PlayerUtil.clearPlayer(player);
                    player.getInventory().setItem(0, ItemUtil.createItem(Material.WOOD_SWORD, ChatColor.GREEN + "Wood Sword"));
                    player.getInventory().setItem(1, ItemUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow"));
                    player.getInventory()
                        .setItem(
                            6,
                            ItemUtil.createItem(
                                Material.GLOWSTONE_DUST,
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills",
                                oitcPlayer.getScore() == 0 ? 1 : oitcPlayer.getScore()
                            )
                        );
                    player.getInventory()
                        .setItem(7, ItemUtil.createItem(Material.REDSTONE, ChatColor.RED.toString() + ChatColor.BOLD + "Lives", oitcPlayer.getLives()));
                    player.getInventory().setItem(8, new ItemStack(Material.ARROW));
                    player.updateInventory();
                }
            );
    }

    private Player getWinnerPlayer() {
        if (this.getByState(OITCPlayer.OITCState.FIGHTING).size() == 0) {
            return null;
        } else {
            List<OITCPlayer> fighting = this.sortedScores();
            return this.getPlugin().getServer().getPlayer(fighting.get(0).getUuid());
        }
    }

    private List<UUID> getByState(OITCPlayer.OITCState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();
        int playingOITC = this.getPlayers().size();
        strings.add(ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Players§7: " + playingOITC + "/" + this.getLimit());
        int countdown = this.getCountdownTask().getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
        }

        if (this.getPlayer(player) != null) {
            OITCPlayer oitcPlayer = this.getPlayer(player);
            if (oitcPlayer.getState() == OITCPlayer.OITCState.FIGHTING || oitcPlayer.getState() == OITCPlayer.OITCState.RESPAWNING) {
                strings.add(ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Kills§7: " + oitcPlayer.getScore());
                strings.add(ChatColor.RED.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Lives§7: " + oitcPlayer.getLives());
            }
        }

        List<OITCPlayer> sortedList = this.sortedScores();
        if (sortedList.size() >= 2 && this.getState() == EventState.STARTED) {
            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            strings.add(ChatColor.RED.toString() + "TOP KILLS");
            Player first = Bukkit.getPlayer(sortedList.get(0).getUuid());
            Player second = Bukkit.getPlayer(sortedList.get(1).getUuid());
            if (first != null) {
                strings.add(ChatColor.WHITE + "[1] " + first.getName() + "§7: §a" + sortedList.get(0).getScore());
            }

            if (second != null) {
                strings.add(ChatColor.WHITE + "[2] " + second.getName() + "§7: §a" + sortedList.get(1).getScore());
            }

            if (sortedList.size() >= 3) {
                Player third = Bukkit.getPlayer(sortedList.get(2).getUuid());
                if (third != null) {
                    strings.add(ChatColor.WHITE + "[3] " + ChatColor.WHITE + third.getName() + "§7: §a" + sortedList.get(2).getScore());
                }
            }
        }

        return strings;
    }

    public List<OITCPlayer> sortedScores() {
        List<OITCPlayer> list = new ArrayList<>(this.players.values());
        list.sort(new OITCEvent.SortComparator().reversed());
        return list;
    }

    public OITCEvent.OITCGameTask getGameTask() {
        return this.gameTask;
    }

    public class OITCGameTask extends BukkitRunnable {
        private int time = 303;

        public void run() {
            if (this.time == 303) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 302) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 301) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 300) {
                PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", OITCEvent.this.getBukkitPlayers());

                for (OITCPlayer player : OITCEvent.this.getPlayers().values()) {
                    player.setScore(0);
                    player.setLives(5);
                    player.setState(OITCPlayer.OITCState.FIGHTING);
                }

                for (Player player : OITCEvent.this.getBukkitPlayers()) {
                    OITCPlayer oitcPlayer = OITCEvent.this.getPlayer(player.getUniqueId());
                    if (oitcPlayer != null) {
                        OITCEvent.this.teleportNextLocation(player);
                        OITCEvent.this.giveRespawnItems(player, oitcPlayer);
                    }
                }
            } else if (this.time <= 0) {
                Player winner = OITCEvent.this.getWinnerPlayer();
                if (winner != null) {
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);
                    String announce = ChatColor.DARK_RED
                        + winner.getName()
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + "OITC"
                        + ChatColor.WHITE
                        + " event!";
                    Bukkit.broadcastMessage(announce);
                }

                OITCEvent.this.gameTask.cancel();
                OITCEvent.this.end();
                this.cancel();
                return;
            }

            if (OITCEvent.this.getByState(OITCPlayer.OITCState.FIGHTING).size() == 1 || OITCEvent.this.getPlayers().size() == 1) {
                Player winner = new ArrayList<>(OITCEvent.this.players.values()).get(0).getPlayer();
                PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                winnerData.setOitcEventWins(winnerData.getOitcEventWins() + 1);
                String announce = ChatColor.DARK_RED
                    + winner.getName()
                    + ChatColor.WHITE
                    + " has won our "
                    + ChatColor.DARK_RED
                    + "OITC"
                    + ChatColor.WHITE
                    + " event!";
                Bukkit.broadcastMessage(announce);
                this.cancel();
                OITCEvent.this.end();
            }

            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers()
                );
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", OITCEvent.this.getBukkitPlayers()
                );
            }

            this.time--;
        }

        public int getTime() {
            return this.time;
        }
    }

    public class RespawnTask extends BukkitRunnable {
        private final Player player;
        private final OITCPlayer oitcPlayer;
        private int time = 5;

        public void run() {
            if (this.oitcPlayer.getLives() == 0) {
                this.cancel();
            } else {
                if (this.time > 0) {
                    this.player.sendMessage(ChatColor.RED + "[Event] " + ChatColor.GRAY + "Respawning in " + this.time + "...");
                }

                if (this.time == 5) {
                    OITCEvent.this.getPlugin().getServer().getScheduler().runTask(OITCEvent.this.getPlugin(), () -> {
                        PlayerUtil.clearPlayer(this.player);
                        OITCEvent.this.getBukkitPlayers().forEach(member -> member.hidePlayer(this.player));
                        OITCEvent.this.getBukkitPlayers().forEach(this.player::hidePlayer);
                        this.player.setGameMode(GameMode.SPECTATOR);
                    });
                    this.oitcPlayer.setState(OITCPlayer.OITCState.RESPAWNING);
                } else if (this.time <= 0) {
                    this.player.sendMessage(ChatColor.RED + "[Event] " + ChatColor.GRAY + "Respawning...");
                    this.player
                        .sendMessage(
                            ChatColor.RED
                                + "[Event] "
                                + ChatColor.RED.toString()
                                + ChatColor.BOLD
                                + this.oitcPlayer.getLives()
                                + " "
                                + (this.oitcPlayer.getLives() == 1 ? "LIFE" : "LIVES")
                                + " REMAINING"
                        );
                    OITCEvent.this.getPlugin()
                        .getServer()
                        .getScheduler()
                        .runTaskLater(
                            OITCEvent.this.getPlugin(),
                            () -> {
                                OITCEvent.this.giveRespawnItems(this.player, this.oitcPlayer);
                                this.player
                                    .teleport(
                                        OITCEvent.this.getGameLocations()
                                            .remove(ThreadLocalRandom.current().nextInt(OITCEvent.this.getGameLocations().size()))
                                            .toBukkitLocation()
                                    );
                                OITCEvent.this.getBukkitPlayers().forEach(member -> member.showPlayer(this.player));
                                OITCEvent.this.getBukkitPlayers().forEach(this.player::showPlayer);
                            },
                            2L
                        );
                    this.oitcPlayer.setState(OITCPlayer.OITCState.FIGHTING);
                    this.cancel();
                }

                this.time--;
            }
        }

        public Player getPlayer() {
            return this.player;
        }

        public OITCPlayer getOitcPlayer() {
            return this.oitcPlayer;
        }

        public int getTime() {
            return this.time;
        }

        public RespawnTask(Player player, OITCPlayer oitcPlayer) {
            this.player = player;
            this.oitcPlayer = oitcPlayer;
        }
    }

    private class SortComparator implements Comparator<OITCPlayer> {
        private SortComparator() {
        }

        public int compare(OITCPlayer p1, OITCPlayer p2) {
            return Integer.compare(p1.getScore(), p2.getScore());
        }
    }
}
