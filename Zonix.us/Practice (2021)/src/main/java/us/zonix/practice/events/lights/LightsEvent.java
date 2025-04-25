package us.zonix.practice.events.lights;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.maiko.dexter.profile.Profile;
import me.maiko.dexter.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventCountdownTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.util.DateUtil;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;

public class LightsEvent extends PracticeEvent<LightsPlayer> {
    private final Map<UUID, LightsPlayer> players = new HashMap<>();
    private final List<UUID> movingPlayers = new ArrayList<>();
    private LightsEvent.LightsGameTask gameTask = null;
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);
    private int countdown;
    private int taskId;
    private long ticksPerRound;
    private int status;
    private LightsEvent.LightsGameState current;
    private long timeLeft;
    private Random random = new Random();

    public LightsEvent() {
        super("RedLightGreenLight", ItemUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.RED + "RedLightGreenLight Event"), true);
    }

    @Override
    public Map<UUID, LightsPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getLightsLocation());
    }

    @Override
    public void onStart() {
        this.gameTask = new LightsEvent.LightsGameTask();
        this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
        this.taskId = this.gameTask.getTaskId();
        this.current = LightsEvent.LightsGameState.GREEN;
        this.countdown = 0;
        this.ticksPerRound = 200L;
        this.status = -1;
        this.timeLeft = 0L;
        this.movingPlayers.clear();
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> {
            this.players.put(player.getUniqueId(), new LightsPlayer(player.getUniqueId(), this));
            this.getPlayer(player).setState(LightsPlayer.LightsState.LOBBY);
        };
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            LightsPlayer data = this.getPlayer(player);
            if (data.getState() != LightsPlayer.LightsState.LOBBY) {
                this.players.remove(player.getUniqueId());
                this.sendMessage(new String[]{"&7[&f" + this.getName() + "&7] &c" + player.getName() + ChatColor.WHITE + " has been eliminated from the game."});
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.awardCoins(player, 5);
                player.sendMessage(CC.GOLD + "You earn 5 coins for participating in the event.");
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
                                    .addSpectatorLights(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
                            }
                        }
                    );
                if (this.getByState(LightsPlayer.LightsState.INGAME).size() == 1) {
                    Player winner = Bukkit.getPlayer(this.getByState(LightsPlayer.LightsState.INGAME).stream().findFirst().get());
                    if (winner != null) {
                        String announce = ChatColor.DARK_RED
                            + winner.getName()
                            + ChatColor.WHITE
                            + " has won our "
                            + ChatColor.DARK_RED
                            + "RedLightGreenLight"
                            + ChatColor.WHITE
                            + " event!";
                        Profile winnerProfile = Profile.getByUuid(winner.getUniqueId());
                        winnerProfile.awardCoins(winner, 15);
                        winner.sendMessage(CC.GOLD + "You earn 15 coins for winning the event!");
                        Bukkit.broadcastMessage(announce);
                    }

                    this.end();
                }
            }
        };
    }

    public void setCurrent(LightsEvent.LightsGameState state) {
        this.current = state;

        for (Player player : this.getBukkitPlayers()) {
            this.giveItems(player);
        }
    }

    public void teleportToSpawn(Player player) {
        Bukkit.getServer()
            .getScheduler()
            .runTask(this.getPlugin(), () -> player.teleport(this.getPlugin().getSpawnManager().getLightsStart().toBukkitLocation().clone().add(0.0, 2.0, 0.0)));
    }

    private void giveItems(Player player) {
        this.getPlugin()
            .getServer()
            .getScheduler()
            .runTask(
                this.getPlugin(),
                () -> {
                    for (int i = 0; i <= 8; i++) {
                        player.getInventory()
                            .setItem(
                                i,
                                ItemUtil.createItem(
                                    Material.WOOL,
                                    this.current == LightsEvent.LightsGameState.GREEN
                                        ? ChatColor.GREEN.toString() + ChatColor.BOLD + "GO"
                                        : (
                                            this.current == LightsEvent.LightsGameState.YELLOW
                                                ? ChatColor.YELLOW.toString() + ChatColor.BOLD + "SLOW"
                                                : ChatColor.RED.toString() + ChatColor.BOLD + "STOP"
                                        ),
                                    1,
                                    (short)(
                                        this.current == LightsEvent.LightsGameState.GREEN ? 5 : (this.current == LightsEvent.LightsGameState.YELLOW ? 4 : 14)
                                    )
                                )
                            );
                    }

                    player.updateInventory();
                }
            );
    }

    public List<UUID> getByState(LightsPlayer.LightsState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public LightsEvent.LightsGameState getCurrent() {
        return this.current;
    }

    public List<UUID> getMovingPlayers() {
        return this.movingPlayers;
    }

    public String getTimeLeft() {
        long time = this.timeLeft - System.currentTimeMillis();
        if (time >= 3600000L) {
            return DateUtil.formatTime(time);
        } else if (time >= 60000L) {
            return DateUtil.formatTime(time);
        } else {
            DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");
            return SECONDS_FORMATTER.format((double)((float)time / 1000.0F)) + "s";
        }
    }

    private int getRandomNumber() {
        return this.random.nextInt(4) + 1;
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();
        int playing = this.getPlayers().size();
        strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Players§7: " + playing + "/" + this.getLimit());
        int countdown = this.getCountdownTask().getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
        }

        if (this.getPlayer(player) != null && this.getPlayer(player).getState() == LightsPlayer.LightsState.INGAME) {
            strings.add(" ");
            if (this.getCurrent() == LightsEvent.LightsGameState.RED) {
                strings.add("      §4⬤  §4§lSTOP");
                strings.add("      §7⬤");
                strings.add("      §7⬤");
            } else if (this.getCurrent() == LightsEvent.LightsGameState.YELLOW) {
                strings.add("      §7⬤");
                strings.add("      §e⬤  §6§lSLOW");
                strings.add("      §7⬤");
            } else if (this.getCurrent() == LightsEvent.LightsGameState.GREEN) {
                strings.add("      §7⬤");
                strings.add("      §7⬤");
                strings.add("      §a⬤  §a§lGO");
            }

            strings.add(" ");
        }

        return strings;
    }

    public LightsEvent.LightsGameTask getGameTask() {
        return this.gameTask;
    }

    public static enum LightsGameState {
        GREEN,
        YELLOW,
        RED;
    }

    public class LightsGameTask extends BukkitRunnable {
        public LightsGameTask() {
            LightsEvent.this.ticksPerRound = 150L;
            LightsEvent.this.status = -1;
            LightsEvent.this.countdown = 3;
        }

        public void run() {
            if (LightsEvent.this.getPlayers().size() == 1) {
                Player winner = Bukkit.getPlayer(LightsEvent.this.getByState(LightsPlayer.LightsState.INGAME).get(0));
                if (winner != null) {
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setWaterDropEventWins(winnerData.getWaterDropEventWins() + 1);
                    Bukkit.broadcastMessage(
                        ChatColor.DARK_RED
                            + winner.getName()
                            + ChatColor.WHITE
                            + " has won a "
                            + ChatColor.DARK_RED
                            + LightsEvent.this.getName()
                            + ChatColor.WHITE
                            + " event!"
                    );
                    Profile winnerProfile = Profile.getByUuid(winner.getUniqueId());
                    winnerProfile.awardCoins(winner, 15);
                    winner.sendMessage(CC.GOLD + "You earn 15 coins for winning the event!");
                }

                LightsEvent.this.end();
                this.cancel();
            } else {
                if (LightsEvent.this.status == -1) {
                    if (LightsEvent.this.countdown == 3) {
                        PlayerUtil.sendMessage(
                            ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", LightsEvent.this.getBukkitPlayers()
                        );
                    } else if (LightsEvent.this.countdown == 2) {
                        PlayerUtil.sendMessage(
                            ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", LightsEvent.this.getBukkitPlayers()
                        );
                    } else if (LightsEvent.this.countdown == 1) {
                        PlayerUtil.sendMessage(
                            ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", LightsEvent.this.getBukkitPlayers()
                        );
                    } else if (LightsEvent.this.countdown == 0) {
                        PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", LightsEvent.this.getBukkitPlayers());
                        LightsEvent.this.status++;

                        for (LightsPlayer player : LightsEvent.this.getPlayers().values()) {
                            player.setState(LightsPlayer.LightsState.INGAME);
                        }

                        for (Player online : LightsEvent.this.getBukkitPlayers()) {
                            LightsEvent.this.teleportToSpawn(online);
                        }

                        Bukkit.getScheduler().cancelTask(LightsEvent.this.taskId);
                        LightsEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(LightsEvent.this.getPlugin(), this, 10L).getTaskId();
                    }

                    LightsEvent.this.countdown--;
                } else if (LightsEvent.this.status == 0) {
                    LightsEvent.this.movingPlayers.clear();
                    LightsEvent.this.status++;
                    LightsEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "RedLightGreenLight"
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GREEN.toString()
                                + ChatColor.BOLD
                                + "GO GO GO"
                        }
                    );
                    LightsEvent.this.setCurrent(LightsEvent.LightsGameState.GREEN);
                    LightsEvent.this.ticksPerRound = LightsEvent.this.ticksPerRound - (long)(LightsEvent.this.getRandomNumber() * 20);
                    LightsEvent.this.timeLeft = System.currentTimeMillis() + LightsEvent.this.ticksPerRound / 20L * 1000L;
                    Bukkit.getScheduler().cancelTask(LightsEvent.this.taskId);
                    LightsEvent.this.taskId = Bukkit.getScheduler()
                        .runTaskLaterAsynchronously(LightsEvent.this.getPlugin(), this, LightsEvent.this.ticksPerRound)
                        .getTaskId();
                } else if (LightsEvent.this.status == 1) {
                    LightsEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "RedLightGreenLight"
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.YELLOW.toString()
                                + ChatColor.BOLD
                                + "SLOW DOWN"
                        }
                    );
                    LightsEvent.this.setCurrent(LightsEvent.LightsGameState.YELLOW);
                    LightsEvent.this.status++;
                    LightsEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(LightsEvent.this.getPlugin(), this, 30L).getTaskId();
                } else if (LightsEvent.this.status == 2) {
                    LightsEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "RedLightGreenLight"
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.RED.toString()
                                + ChatColor.BOLD
                                + "STOP"
                        }
                    );
                    LightsEvent.this.setCurrent(LightsEvent.LightsGameState.RED);
                    LightsEvent.this.status++;
                    LightsEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(LightsEvent.this.getPlugin(), this, 50L).getTaskId();
                } else if (LightsEvent.this.status == 3) {
                    LightsEvent.this.status = 0;
                    LightsEvent.this.ticksPerRound = 150L;
                    LightsEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(LightsEvent.this.getPlugin(), this, 10L).getTaskId();
                }
            }
        }
    }
}
