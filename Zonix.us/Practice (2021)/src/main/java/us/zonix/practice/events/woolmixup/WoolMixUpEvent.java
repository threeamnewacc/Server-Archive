package us.zonix.practice.events.woolmixup;

import com.boydti.fawe.util.EditSessionBuilder;
import com.boydti.fawe.util.TaskManager;
import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.maiko.dexter.profile.Profile;
import me.maiko.dexter.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import us.zonix.practice.util.DateUtil;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;

public class WoolMixUpEvent extends PracticeEvent<WoolMixUpPlayer> {
    private final Map<UUID, WoolMixUpPlayer> players = new HashMap<>();
    private Map<Integer, String> colors = new HashMap<Integer, String>() {
        {
            this.put(Integer.valueOf(11), ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE");
            this.put(Integer.valueOf(14), ChatColor.RED.toString() + ChatColor.BOLD + "RED");
            this.put(Integer.valueOf(9), ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "CYAN");
            this.put(Integer.valueOf(15), ChatColor.BLACK.toString() + ChatColor.BOLD + "BLACK");
            this.put(Integer.valueOf(13), ChatColor.GREEN.toString() + ChatColor.BOLD + "GREEN");
            this.put(Integer.valueOf(4), ChatColor.YELLOW.toString() + ChatColor.BOLD + "YELLOW");
            this.put(Integer.valueOf(1), ChatColor.GOLD.toString() + ChatColor.BOLD + "ORANGE");
            this.put(Integer.valueOf(10), ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "PURPLE");
            this.put(Integer.valueOf(0), ChatColor.WHITE.toString() + ChatColor.BOLD + "WHITE");
            this.put(Integer.valueOf(6), ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "PINK");
        }
    };
    private HashMap<Location, Integer> blocks = new HashMap<>();
    private HashMap<Location, Integer> blocksRegen = new HashMap<>();
    private WoolMixUpEvent.WoolMixUpGameTask gameTask = null;
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);
    private List<UUID> visibility;
    private int countdown;
    private int taskId;
    private int currentColor = -1;
    private long ticksPerRound;
    private int status;
    private boolean isShuffling;
    private int round;
    private long timeLeft;
    private Random random = new Random();

    public WoolMixUpEvent() {
        super("BlockParty", ItemUtil.createItem(Material.WOOL, ChatColor.RED + "BlockParty Event"), true);
    }

    @Override
    public Map<UUID, WoolMixUpPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getWoolLocation());
    }

    @Override
    public void onStart() {
        this.gameTask = new WoolMixUpEvent.WoolMixUpGameTask();
        this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
        this.taskId = this.gameTask.getTaskId();
        this.visibility = new ArrayList<>();
        this.blocks.clear();
        this.blocksRegen.clear();
        this.setupAllBlocks();
        this.currentColor = -1;
        this.countdown = 0;
        this.ticksPerRound = 200L;
        this.status = -1;
        this.isShuffling = false;
        this.round = 0;
        this.timeLeft = 0L;
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> {
            this.players.put(player.getUniqueId(), new WoolMixUpPlayer(player.getUniqueId(), this));
            this.getPlayer(player).setState(WoolMixUpPlayer.WoolMixUpState.LOBBY);
        };
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            WoolMixUpPlayer data = this.getPlayer(player);
            if (data.getState() != WoolMixUpPlayer.WoolMixUpState.LOBBY) {
                this.getPlayers().remove(player.getUniqueId());
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.awardCoins(player, 5);
                player.sendMessage(CC.GOLD + "You earn 5 coins for participating in the event.");
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
                            + " has been eliminated from the game."
                    }
                );
                this.getPlugin()
                    .getServer()
                    .getScheduler()
                    .runTaskLater(
                        this.getPlugin(),
                        () -> {
                            this.getPlugin().getPlayerManager().sendToSpawnAndReset(player);
                            if (this.getPlayers().size() >= 2) {
                                this.getPlugin()
                                    .getEventManager()
                                    .addSpectatorWoolMixUp(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
                            }
                        },
                        20L
                    );
                if (this.getPlayers().size() == 1) {
                    Player winner = this.players.values().stream().findFirst().get().getPlayer();
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setWaterDropEventWins(winnerData.getWaterDropEventWins() + 1);
                    String announce = ChatColor.DARK_RED
                        + winner.getName()
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + "BlockParty"
                        + ChatColor.WHITE
                        + " event!";
                    Profile winnerProfile = Profile.getByUuid(winner.getUniqueId());
                    winnerProfile.awardCoins(winner, 15);
                    winner.sendMessage(CC.GOLD + "You earn 15 coins for winning the event!");
                    Bukkit.broadcastMessage(announce);
                    this.isShuffling = false;
                    this.end();
                }
            }
        };
    }

    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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
            .runTask(this.getPlugin(), () -> player.teleport(this.getPlugin().getSpawnManager().getWoolCenter().toBukkitLocation().clone().add(0.0, 2.0, 0.0)));
    }

    private void giveItems(Player player) {
        this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            if (this.currentColor >= 0 && this.isShuffling) {
                for (int i = 0; i <= 6; i++) {
                    player.getInventory().setItem(i, ItemUtil.createItem(Material.WOOL, this.colors.get(this.currentColor), 1, (short)this.currentColor));
                }
            }

            player.getInventory().setItem(7, ItemUtil.createItem(Material.FIREBALL, ChatColor.GREEN.toString() + "Toggle Visibility"));
            player.getInventory().setItem(8, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Event"));
            player.updateInventory();
        });
    }

    public List<UUID> getByState(WoolMixUpPlayer.WoolMixUpState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    public int getRound() {
        return this.round;
    }

    public HashMap<Location, Integer> getBlocksRegen() {
        return this.blocksRegen;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public void generateArena(Location location) {
        int x = location.getBlockX() - 32;
        int y = location.getBlockY();
        int y_ = location.getBlockY() - 4;
        int z = location.getBlockZ() - 32;
        int current = 0;
        List<Integer> data = this.getDataFromWool(location);

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int x_ = x + i * 4;
                int z_ = z + j * 4;
                int newCurrent = this.getRandomColor();
                if (current == newCurrent) {
                    if (newCurrent > 0) {
                        newCurrent--;
                    } else {
                        newCurrent += 2;
                    }
                }

                if (data.size() > 15 && data.get(data.size() - 16) == newCurrent) {
                    if (newCurrent > 0) {
                        newCurrent--;
                    } else {
                        newCurrent += 2;
                    }
                }

                current = newCurrent;
                data.add(current);
                int finalCurrent = current;
                TaskManager.IMP
                    .async(
                        () -> {
                            EditSession editSession = new EditSessionBuilder(location.getWorld().getName())
                                .fastmode(true)
                                .allowedRegionsEverywhere()
                                .autoQueue(false)
                                .limitUnlimited()
                                .build();

                            for (int i_ = 0; i_ < 4; i_++) {
                                for (int j_ = 0; j_ < 4; j_++) {
                                    Block b = location.getWorld()
                                        .getBlockAt(new Location(location.getWorld(), (double)(x_ + i_), (double)y, (double)(z_ + j_)));
                                    Block b_ = location.getWorld()
                                        .getBlockAt(new Location(location.getWorld(), (double)(x_ + i_), (double)y_, (double)(z_ + j_)));

                                    try {
                                        editSession.setBlock(
                                            new Vector((double)b.getLocation().getBlockX(), (double)b.getLocation().getBlockY(), b.getLocation().getZ()),
                                            new BaseBlock(35, finalCurrent)
                                        );
                                        editSession.setBlock(
                                            new Vector((double)b_.getLocation().getBlockX(), (double)b_.getLocation().getBlockY(), b_.getLocation().getZ()),
                                            new BaseBlock(7, 0)
                                        );
                                    } catch (MaxChangedBlocksException var12x) {
                                        var12x.printStackTrace();
                                    }
                                }
                            }

                            editSession.flushQueue();
                        }
                    );
            }
        }
    }

    private void setupAllBlocks() {
        int minX = this.getPlugin().getSpawnManager().getWoolMin().toBukkitLocation().getBlockX();
        int minY = this.getPlugin().getSpawnManager().getWoolMin().toBukkitLocation().getBlockY();
        int minZ = this.getPlugin().getSpawnManager().getWoolMin().toBukkitLocation().getBlockZ();
        int maxX = this.getPlugin().getSpawnManager().getWoolMax().toBukkitLocation().getBlockX();
        int maxY = this.getPlugin().getSpawnManager().getWoolMax().toBukkitLocation().getBlockY();
        int maxZ = this.getPlugin().getSpawnManager().getWoolMax().toBukkitLocation().getBlockZ();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location loc = new Location(this.getPlugin().getSpawnManager().getWoolCenter().toBukkitWorld(), (double)x, (double)y, (double)z);
                    if (loc.getBlock().getType() == Material.WOOL) {
                        this.blocks.put(loc.getBlock().getLocation(), Integer.valueOf(loc.getBlock().getData()));
                    }
                }
            }
        }
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

        if (this.getPlayer(player) != null && this.getPlayer(player).getState() == WoolMixUpPlayer.WoolMixUpState.INGAME) {
            String color = this.getCurrentColor();
            String timeLeft = this.getTimeLeft();
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Round§7: " + this.getRound());
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Color§7: " + color);
            if (!timeLeft.contains("-")) {
                strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Time Left§7: " + timeLeft);
            }
        }

        return strings;
    }

    private void removeAllExceptOne(int currentColor) {
        HashMap<Location, Integer> blocksToRemove = new HashMap<>();

        for (Entry<Location, Integer> entry : this.blocks.entrySet()) {
            if (entry.getKey().getBlock().getType() == Material.WOOL && entry.getValue() != currentColor) {
                blocksToRemove.put(entry.getKey().getBlock().getLocation(), entry.getValue());
            }
        }

        this.blocksRegen.clear();
        this.blocksRegen.putAll(blocksToRemove);
        TaskManager.IMP
            .async(
                () -> {
                    EditSession editSession = new EditSessionBuilder(this.getPlugin().getSpawnManager().getWoolCenter().getWorld())
                        .fastmode(true)
                        .allowedRegionsEverywhere()
                        .autoQueue(false)
                        .limitUnlimited()
                        .build();

                    for (Location block : blocksToRemove.keySet()) {
                        try {
                            editSession.setBlock(new Vector((double)block.getBlockX(), (double)block.getBlockY(), block.getZ()), new BaseBlock(0, 0));
                        } catch (MaxChangedBlocksException var6) {
                            var6.printStackTrace();
                        }
                    }

                    editSession.flushQueue();
                }
            );
    }

    public String getCurrentColor() {
        return this.currentColor == -1 ? ChatColor.GRAY.toString() + ChatColor.BOLD + "None" : this.colors.get(this.currentColor);
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

    public HashMap<Location, Integer> getBlocks() {
        return this.blocks;
    }

    public void regenerateArena(HashMap<Location, Integer> blocksToAdd) {
        TaskManager.IMP
            .async(
                () -> {
                    EditSession editSession = new EditSessionBuilder(this.getPlugin().getSpawnManager().getWoolCenter().getWorld())
                        .fastmode(true)
                        .allowedRegionsEverywhere()
                        .autoQueue(false)
                        .limitUnlimited()
                        .build();

                    try {
                        for (Location block : blocksToAdd.keySet()) {
                            editSession.setBlock(new Vector(block.getBlockX(), block.getBlockY(), block.getBlockZ()), new BaseBlock(35, blocksToAdd.get(block)));
                        }
                    } catch (MaxChangedBlocksException var5) {
                        var5.printStackTrace();
                    }

                    editSession.flushQueue();
                }
            );
    }

    private List<Integer> getDataFromWool(Location location) {
        List<Integer> woolData = new ArrayList<>();
        int x = location.getBlockX() - 32;
        int y = location.getBlockY();
        int z = location.getBlockZ() - 32;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int x_ = x + i * 4;
                int z_ = z + j * 4;
                Block b = location.getWorld().getBlockAt(new Location(location.getWorld(), (double)x_, (double)y, (double)z_));
                woolData.add(Integer.valueOf(b.getData()));
            }
        }

        return woolData;
    }

    private int getRandomColor() {
        List<Integer> integers = new ArrayList<>();
        if (this.colors != null) {
            integers.addAll(this.colors.keySet());
            Collections.shuffle(integers);
        }

        return integers.get(this.random.nextInt(integers.size()));
    }

    public WoolMixUpEvent.WoolMixUpGameTask getGameTask() {
        return this.gameTask;
    }

    public class WoolMixUpGameTask extends BukkitRunnable {
        public WoolMixUpGameTask() {
            WoolMixUpEvent.this.ticksPerRound = 200L;
            WoolMixUpEvent.this.status = -1;
            WoolMixUpEvent.this.round = 0;
            WoolMixUpEvent.this.countdown = 3;
        }

        public void run() {
            if (WoolMixUpEvent.this.getPlayers().size() == 1) {
                Player winner = WoolMixUpEvent.this.players.values().stream().findFirst().get().getPlayer();
                if (winner != null) {
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setWaterDropEventWins(winnerData.getWaterDropEventWins() + 1);
                    String announce = ChatColor.DARK_RED
                        + winner.getName()
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + "BlockParty"
                        + ChatColor.WHITE
                        + " event!";
                    Profile winnerProfile = Profile.getByUuid(winner.getUniqueId());
                    winnerProfile.awardCoins(winner, 15);
                    winner.sendMessage(CC.GOLD + "You earn 15 coins for winning the event!");
                    Bukkit.broadcastMessage(announce);
                }

                WoolMixUpEvent.this.isShuffling = false;
                WoolMixUpEvent.this.end();
                this.cancel();
            } else {
                if (WoolMixUpEvent.this.status == -1) {
                    if (WoolMixUpEvent.this.countdown == 3) {
                        PlayerUtil.sendMessage(
                            ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", WoolMixUpEvent.this.getBukkitPlayers()
                        );
                    } else if (WoolMixUpEvent.this.countdown == 2) {
                        PlayerUtil.sendMessage(
                            ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", WoolMixUpEvent.this.getBukkitPlayers()
                        );
                    } else if (WoolMixUpEvent.this.countdown == 1) {
                        PlayerUtil.sendMessage(
                            ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", WoolMixUpEvent.this.getBukkitPlayers()
                        );
                    } else if (WoolMixUpEvent.this.countdown == 0) {
                        PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", WoolMixUpEvent.this.getBukkitPlayers());
                        WoolMixUpEvent.this.status++;

                        for (WoolMixUpPlayer player : WoolMixUpEvent.this.getPlayers().values()) {
                            player.setState(WoolMixUpPlayer.WoolMixUpState.INGAME);
                        }

                        for (Player online : WoolMixUpEvent.this.getBukkitPlayers()) {
                            WoolMixUpEvent.this.teleportToSpawn(online);
                            WoolMixUpEvent.this.giveItems(online);
                        }

                        Bukkit.getScheduler().cancelTask(WoolMixUpEvent.this.taskId);
                        WoolMixUpEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(WoolMixUpEvent.this.getPlugin(), this, 10L).getTaskId();
                    }

                    WoolMixUpEvent.this.countdown--;
                } else if (WoolMixUpEvent.this.status == 0) {
                    WoolMixUpEvent.this.status++;
                    WoolMixUpEvent.this.round++;
                    WoolMixUpEvent.this.countdown = 3;
                    WoolMixUpEvent.this.currentColor = -1;
                    WoolMixUpEvent.this.taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(WoolMixUpEvent.this.getPlugin(), this, 0L, 20L).getTaskId();
                } else if (WoolMixUpEvent.this.status == 1) {
                    WoolMixUpEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + WoolMixUpEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GRAY
                                + "Choosing color in "
                                + ChatColor.RED.toString()
                                + ChatColor.BOLD
                                + WoolMixUpEvent.this.countdown
                        }
                    );
                    WoolMixUpEvent.this.countdown--;
                    if (WoolMixUpEvent.this.countdown == 0) {
                        WoolMixUpEvent.this.status++;
                        Bukkit.getScheduler().cancelTask(WoolMixUpEvent.this.taskId);
                        WoolMixUpEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(WoolMixUpEvent.this.getPlugin(), this, 10L).getTaskId();
                    }
                } else if (WoolMixUpEvent.this.status == 2) {
                    WoolMixUpEvent.this.currentColor = WoolMixUpEvent.this.getRandomColor();
                    WoolMixUpEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + WoolMixUpEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GRAY
                                + "The next color is "
                                + WoolMixUpEvent.this.colors.get(WoolMixUpEvent.this.currentColor)
                        }
                    );
                    WoolMixUpEvent.this.isShuffling = true;

                    for (Player online : WoolMixUpEvent.this.getBukkitPlayers()) {
                        WoolMixUpEvent.this.giveItems(online);
                    }

                    if (WoolMixUpEvent.this.round > 1 && WoolMixUpEvent.this.round <= 8) {
                        WoolMixUpEvent.this.ticksPerRound = WoolMixUpEvent.this.ticksPerRound - (long)((double)WoolMixUpEvent.this.ticksPerRound * 0.3);
                    }

                    WoolMixUpEvent.this.timeLeft = System.currentTimeMillis() + WoolMixUpEvent.this.ticksPerRound / 20L * 1000L;
                    WoolMixUpEvent.this.status++;
                    WoolMixUpEvent.this.taskId = Bukkit.getScheduler()
                        .runTaskLaterAsynchronously(WoolMixUpEvent.this.getPlugin(), this, WoolMixUpEvent.this.ticksPerRound)
                        .getTaskId();
                } else if (WoolMixUpEvent.this.status == 3) {
                    WoolMixUpEvent.this.isShuffling = false;

                    for (Player online : WoolMixUpEvent.this.getBukkitPlayers()) {
                        WoolMixUpEvent.this.giveItems(online);
                    }

                    WoolMixUpEvent.this.removeAllExceptOne(WoolMixUpEvent.this.currentColor);
                    WoolMixUpEvent.this.status++;
                    WoolMixUpEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(WoolMixUpEvent.this.getPlugin(), this, 60L).getTaskId();
                } else if (WoolMixUpEvent.this.status == 4) {
                    WoolMixUpEvent.this.regenerateArena(WoolMixUpEvent.this.blocksRegen);
                    WoolMixUpEvent.this.status = 0;
                    WoolMixUpEvent.this.taskId = Bukkit.getScheduler().runTaskLaterAsynchronously(WoolMixUpEvent.this.getPlugin(), this, 5L).getTaskId();
                }
            }
        }
    }
}
