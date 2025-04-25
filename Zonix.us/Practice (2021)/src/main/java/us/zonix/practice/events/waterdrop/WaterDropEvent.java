package us.zonix.practice.events.waterdrop;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventCountdownTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.util.BlockUtil;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;
import us.zonix.practice.util.cuboid.Cuboid;

public class WaterDropEvent extends PracticeEvent<WaterDropPlayer> {
    private final Map<UUID, WaterDropPlayer> players = new HashMap<>();
    private List<UUID> possiblePlayers = new ArrayList<>();
    private WaterDropEvent.WaterDropGameTask gameTask = null;
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);
    private WaterDropEvent.WaterDropCheckTask waterCheckTask;
    private List<UUID> visibility;
    private int round;
    private Cuboid cuboid;

    public WaterDropEvent() {
        super("WaterDrop", ItemUtil.createItem(Material.BUCKET, ChatColor.RED + "WaterDrop Event"), false);
    }

    @Override
    public Map<UUID, WaterDropPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getWaterDropLocation());
    }

    @Override
    public void onStart() {
        this.gameTask = new WaterDropEvent.WaterDropGameTask();
        this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
        this.waterCheckTask = new WaterDropEvent.WaterDropCheckTask();
        this.waterCheckTask.runTaskTimer(this.getPlugin(), 0L, 10L);
        this.visibility = new ArrayList<>();
        this.round = 0;
        this.possiblePlayers.clear();
        this.cuboid = new Cuboid(
            this.getPlugin().getSpawnManager().getWaterDropFirst().toBukkitLocation(),
            this.getPlugin().getSpawnManager().getWaterDropSecond().toBukkitLocation()
        );
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> {
            this.players.put(player.getUniqueId(), new WaterDropPlayer(player.getUniqueId(), this));
            this.getPlayer(player).setState(WaterDropPlayer.WaterDropState.LOBBY);
        };
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            WaterDropPlayer data = this.getPlayer(player);
            if (data.getState() != WaterDropPlayer.WaterDropState.LOBBY) {
                this.possiblePlayers.add(player.getUniqueId());
                this.teleportToSpawn(player);
                this.giveItems(player);
                this.sendMessage(
                    new String[]{
                        ChatColor.GRAY
                            + "["
                            + ChatColor.YELLOW
                            + "Round "
                            + this.round
                            + ChatColor.GRAY
                            + "] "
                            + ChatColor.RED
                            + player.getName()
                            + " didn't make it to the next round."
                    }
                );
            }
        };
    }

    public void toggleVisibility(Player player) {
        if (this.visibility.contains(player.getUniqueId())) {
            for (Player playing : this.getBukkitPlayers()) {
                player.showPlayer(playing);
            }

            this.visibility.remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now showing players.");
        } else {
            for (Player playing : this.getBukkitPlayers()) {
                player.hidePlayer(playing);
            }

            this.visibility.add(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You are now hiding players.");
        }
    }

    public void teleportToSpawn(Player player) {
        Bukkit.getServer()
            .getScheduler()
            .runTask(this.getPlugin(), () -> player.teleport(this.getPlugin().getSpawnManager().getWaterDropLocation().toBukkitLocation()));
    }

    private void giveItems(Player player) {
        this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, ItemUtil.createItem(Material.FIREBALL, ChatColor.GREEN.toString() + "Toggle Visibility"));
            player.getInventory().setItem(4, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Event"));
            player.updateInventory();
        });
    }

    private void nextRound() {
        List<Player> waterDropPlayers = this.prepareNextRoundPlayers();
        if (waterDropPlayers.size() == 0) {
            this.endGame();
        } else {
            this.sendMessage(
                new String[]{
                    ChatColor.GRAY + "[" + ChatColor.YELLOW + "Round " + this.round + ChatColor.GRAY + "] " + ChatColor.GOLD + "Checking players left..."
                }
            );

            for (Player player : waterDropPlayers) {
                this.getPlayer(player).setState(WaterDropPlayer.WaterDropState.JUMPING);
                Bukkit.getServer()
                    .getScheduler()
                    .runTask(this.getPlugin(), () -> player.teleport(this.getPlugin().getSpawnManager().getWaterDropJump().toBukkitLocation()));
            }

            this.sendMessage(
                new String[]{
                    ChatColor.GRAY
                        + "["
                        + ChatColor.YELLOW
                        + "Round "
                        + this.round
                        + ChatColor.GRAY
                        + "] "
                        + ChatColor.GOLD
                        + this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size()
                        + " players remaining."
                }
            );
        }
    }

    private List<Player> prepareNextRoundPlayers() {
        List<Player> waterDropPlayers = new ArrayList<>();
        if (this.getByState(WaterDropPlayer.WaterDropState.NEXT_ROUND).size() == 1 && this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() == 0) {
            this.endGame();
            return waterDropPlayers;
        } else if (this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() == 0
            && this.getByState(WaterDropPlayer.WaterDropState.NEXT_ROUND).size() == 0) {
            if (this.possiblePlayers.size() <= 1) {
                this.endGame();
                return waterDropPlayers;
            } else {
                for (UUID uuid : this.possiblePlayers) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        waterDropPlayers.add(player);
                    }
                }

                this.round++;
                this.generateCuboid(this.cuboid);
                this.possiblePlayers.clear();
                return waterDropPlayers;
            }
        } else {
            if (this.getByState(WaterDropPlayer.WaterDropState.NEXT_ROUND).size() > 0) {
                for (UUID uuidx : this.getByState(WaterDropPlayer.WaterDropState.NEXT_ROUND)) {
                    Player player = Bukkit.getPlayer(uuidx);
                    if (player != null) {
                        waterDropPlayers.add(player);
                    }
                }
            } else if (this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() > 0) {
                for (UUID uuidxx : this.getByState(WaterDropPlayer.WaterDropState.JUMPING)) {
                    Player player = Bukkit.getPlayer(uuidxx);
                    if (player != null) {
                        waterDropPlayers.add(player);
                    }
                }
            }

            this.round++;
            this.generateCuboid(this.cuboid);
            this.possiblePlayers.clear();
            return waterDropPlayers;
        }
    }

    private void endGame() {
        Player winner = Bukkit.getPlayer(this.getByState(WaterDropPlayer.WaterDropState.JUMPING).get(0));
        PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
        winnerData.setSumoEventWins(winnerData.getSumoEventWins() + 1);

        for (int i = 0; i <= 2; i++) {
            String announce = ChatColor.RED + winner.getName() + ChatColor.WHITE + " has won the event!";
            Bukkit.broadcastMessage(announce);
        }

        String announce = ChatColor.DARK_RED
            + winner.getName()
            + ChatColor.WHITE
            + " has won our "
            + ChatColor.DARK_RED
            + "Water Drop"
            + ChatColor.WHITE
            + " event!";
        Bukkit.broadcastMessage(announce);
        this.end();
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> strings = Lists.newArrayList();
        int playing = this.getPlayers().size();
        strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Players§7: " + playing + "/" + this.getLimit());
        int countdown = this.getCountdownTask().getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
        }

        if (this.getPlayer(player) != null && this.getPlayer(player).getState() != WaterDropPlayer.WaterDropState.LOBBY) {
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Round§7: " + this.getRound());
        }

        return strings;
    }

    private void generateCuboid(Cuboid cuboid) {
        if (cuboid != null) {
            int blocksAmount = this.getBlocksAmount();
            List<Block> blocks = new ArrayList<>();

            for (Block block : cuboid) {
                blocks.add(block);
            }

            TaskManager.IMP
                .async(
                    () -> {
                        EditSession editSession = new EditSessionBuilder(this.cuboid.getWorld().getName())
                            .fastmode(true)
                            .allowedRegionsEverywhere()
                            .autoQueue(false)
                            .limitUnlimited()
                            .build();

                        for (Block entry : blocks) {
                            try {
                                editSession.setBlock(
                                    new Vector((double)entry.getLocation().getBlockX(), (double)entry.getLocation().getBlockY(), entry.getLocation().getZ()),
                                    new BaseBlock(9, 0)
                                );
                            } catch (Exception var8) {
                            }
                        }

                        Collections.shuffle(blocks);

                        for (int i = 0; i < blocksAmount; i++) {
                            try {
                                editSession.setBlock(
                                    new Vector(
                                        (double)blocks.get(i).getLocation().getBlockX(),
                                        (double)blocks.get(i).getLocation().getBlockY(),
                                        blocks.get(i).getLocation().getZ()
                                    ),
                                    new BaseBlock(35, 0)
                                );
                            } catch (Exception var7) {
                            }
                        }

                        editSession.flushQueue();
                        TaskManager.IMP.task(blocks::clear);
                    }
                );
        }
    }

    private Player getRandomPlayer() {
        List<Player> playersRandom = new ArrayList<>();
        playersRandom.addAll(this.getBukkitPlayers());
        Collections.shuffle(playersRandom);
        return playersRandom.get(ThreadLocalRandom.current().nextInt(playersRandom.size()));
    }

    public Cuboid getCuboid() {
        return this.cuboid;
    }

    private int getBlocksAmount() {
        if (this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() <= 5) {
            return 8;
        } else if (this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() <= 10) {
            return 6;
        } else if (this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() <= 15) {
            return 4;
        } else {
            return this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() <= 20 ? 3 : 1;
        }
    }

    public List<UUID> getByState(WaterDropPlayer.WaterDropState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    public int getRound() {
        return this.round;
    }

    public WaterDropEvent.WaterDropGameTask getGameTask() {
        return this.gameTask;
    }

    public WaterDropEvent.WaterDropCheckTask getWaterCheckTask() {
        return this.waterCheckTask;
    }

    public class WaterDropCheckTask extends BukkitRunnable {
        public void run() {
            if (WaterDropEvent.this.getPlayers().size() > 1) {
                WaterDropEvent.this.getBukkitPlayers()
                    .forEach(
                        player -> {
                            if (WaterDropEvent.this.getPlayer(player) == null
                                || WaterDropEvent.this.getPlayer(player).getState() == WaterDropPlayer.WaterDropState.JUMPING) {
                                if (BlockUtil.isStandingOn(player, Material.WATER) || BlockUtil.isStandingOn(player, Material.STATIONARY_WATER)) {
                                    WaterDropEvent.this.getPlayer(player.getUniqueId()).setState(WaterDropPlayer.WaterDropState.NEXT_ROUND);
                                    WaterDropEvent.this.sendMessage(
                                        new String[]{
                                            ChatColor.GRAY
                                                + "["
                                                + ChatColor.YELLOW
                                                + "Round "
                                                + WaterDropEvent.this.round
                                                + ChatColor.GRAY
                                                + "] "
                                                + ChatColor.GREEN
                                                + player.getName()
                                                + " made it to the next round."
                                        }
                                    );
                                    WaterDropEvent.this.teleportToSpawn(player);
                                }
                            }
                        }
                    );
                if (WaterDropEvent.this.getByState(WaterDropPlayer.WaterDropState.NEXT_ROUND).size() >= 1
                        && WaterDropEvent.this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() == 0
                    || WaterDropEvent.this.getByState(WaterDropPlayer.WaterDropState.NEXT_ROUND).size() == 0
                        && WaterDropEvent.this.getByState(WaterDropPlayer.WaterDropState.JUMPING).size() == 0
                        && WaterDropEvent.this.possiblePlayers.size() >= 1) {
                    Bukkit.getScheduler().runTaskLaterAsynchronously(WaterDropEvent.this.getPlugin(), () -> WaterDropEvent.this.nextRound(), 20L);
                }
            }
        }
    }

    public class WaterDropGameTask extends BukkitRunnable {
        private int time = 303;

        public void run() {
            if (this.time == 303) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", WaterDropEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 302) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", WaterDropEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 301) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", WaterDropEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 300) {
                PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", WaterDropEvent.this.getBukkitPlayers());

                for (WaterDropPlayer player : WaterDropEvent.this.getPlayers().values()) {
                    player.setState(WaterDropPlayer.WaterDropState.JUMPING);
                }

                for (Player player : WaterDropEvent.this.getBukkitPlayers()) {
                    WaterDropEvent.this.giveItems(player);
                }

                WaterDropEvent.this.nextRound();
            } else if (this.time <= 0) {
                Player winner = WaterDropEvent.this.getRandomPlayer();
                if (winner != null) {
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setWaterDropEventWins(winnerData.getWaterDropEventWins() + 1);
                    String announce = ChatColor.DARK_RED
                        + winner.getName()
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + "Water Drop"
                        + ChatColor.WHITE
                        + " event!";
                    Bukkit.broadcastMessage(announce);
                }

                WaterDropEvent.this.end();
                this.cancel();
                return;
            }

            if (WaterDropEvent.this.getPlayers().size() == 1) {
                Player winner = WaterDropEvent.this.getRandomPlayer();
                if (winner != null) {
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setWaterDropEventWins(winnerData.getWaterDropEventWins() + 1);
                    String announce = ChatColor.DARK_RED
                        + winner.getName()
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + "Water Drop"
                        + ChatColor.WHITE
                        + " event!";
                    Bukkit.broadcastMessage(announce);
                }

                WaterDropEvent.this.end();
                this.cancel();
            } else {
                if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
                    PlayerUtil.sendMessage(
                        ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", WaterDropEvent.this.getBukkitPlayers()
                    );
                } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                    PlayerUtil.sendMessage(
                        ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...",
                        WaterDropEvent.this.getBukkitPlayers()
                    );
                }

                this.time--;
            }
        }

        public int getTime() {
            return this.time;
        }
    }
}
