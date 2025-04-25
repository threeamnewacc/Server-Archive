package us.zonix.practice.events.redrover;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.events.EventCountdownTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;

public class RedroverEvent extends PracticeEvent<RedroverPlayer> {
    private final Map<UUID, RedroverPlayer> players = new HashMap<>();
    private final List<UUID> blueTeam = new ArrayList<>();
    private final List<UUID> redTeam = new ArrayList<>();
    UUID streakPlayer = null;
    final List<UUID> fighting = new ArrayList<>();
    private RedroverEvent.RedroverGameTask gameTask = null;
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);

    public RedroverEvent() {
        super("Redrover", ItemUtil.createItem(Material.WOOD_SWORD, ChatColor.RED + "Redrover Event"), false);
    }

    @Override
    public Map<UUID, RedroverPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getRedroverLocation());
    }

    @Override
    public void onStart() {
        this.gameTask = new RedroverEvent.RedroverGameTask();
        this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
        this.fighting.clear();
        this.redTeam.clear();
        this.blueTeam.clear();
        this.generateTeams();
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new RedroverPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            RedroverPlayer data = this.getPlayer(player);
            if (data != null) {
                if (data.getState() == RedroverPlayer.RedroverState.FIGHTING || data.getState() == RedroverPlayer.RedroverState.PREPARING) {
                    if (data.getFightTask() != null) {
                        data.getFightTask().cancel();
                    }

                    if (data.getFightPlayer() != null && data.getFightPlayer().getFightTask() != null) {
                        data.getFightPlayer().getFightTask().cancel();
                    }

                    this.getPlayers().remove(player.getUniqueId());
                    this.sendMessage(
                        new String[]{
                            ChatColor.RED
                                + "[Event] "
                                + ChatColor.RED
                                + player.getName()
                                + ChatColor.GRAY
                                + " has been eliminated"
                                + (
                                    Bukkit.getPlayer(data.getFightPlayer().getUuid()) == null
                                        ? "."
                                        : " by " + ChatColor.GREEN + Bukkit.getPlayer(data.getFightPlayer().getUuid()).getName()
                                )
                        }
                    );
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
                                        .addSpectatorRedrover(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
                                }
                            }
                        );
                    this.fighting.remove(player.getUniqueId());
                    this.redTeam.remove(player.getUniqueId());
                    this.blueTeam.remove(player.getUniqueId());
                    this.prepareNextMatch();
                }
            }
        };
    }

    private CustomLocation[] getGameLocations() {
        return new CustomLocation[]{this.getPlugin().getSpawnManager().getRedroverFirst(), this.getPlugin().getSpawnManager().getRedroverSecond()};
    }

    private void prepareNextMatch() {
        if (this.blueTeam.size() != 0 && this.redTeam.size() != 0) {
            RedroverPlayer redPlayer = this.getPlayer(this.redTeam.get(ThreadLocalRandom.current().nextInt(this.redTeam.size())));
            RedroverPlayer bluePlayer = this.getPlayer(this.blueTeam.get(ThreadLocalRandom.current().nextInt(this.blueTeam.size())));
            if (this.fighting.size() == 1 && this.redTeam.contains(this.fighting.get(0))) {
                redPlayer = this.getPlayer(this.fighting.get(0));
                this.streakPlayer = redPlayer.getUuid();
            } else if (this.fighting.size() == 1 && this.blueTeam.contains(this.fighting.get(0))) {
                bluePlayer = this.getPlayer(this.fighting.get(0));
                this.streakPlayer = bluePlayer.getUuid();
            }

            this.fighting.clear();
            this.fighting.addAll(Arrays.asList(redPlayer.getUuid(), bluePlayer.getUuid()));
            Player picked1 = this.getPlugin().getServer().getPlayer(redPlayer.getUuid());
            Player picked2 = this.getPlugin().getServer().getPlayer(bluePlayer.getUuid());
            redPlayer.setState(RedroverPlayer.RedroverState.PREPARING);
            bluePlayer.setState(RedroverPlayer.RedroverState.PREPARING);
            BukkitTask task = new RedroverEvent.RedroverFightTask(picked1, picked2, redPlayer, bluePlayer).runTaskTimer(this.getPlugin(), 0L, 20L);
            redPlayer.setFightPlayer(bluePlayer);
            bluePlayer.setFightPlayer(redPlayer);
            redPlayer.setFightTask(task);
            bluePlayer.setFightTask(task);
            this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
                Player[] players = new Player[]{picked1, picked2};

                for (Player playerx : players) {
                    if (this.streakPlayer == null || this.streakPlayer != playerx.getUniqueId()) {
                        PlayerUtil.clearPlayer(playerx);
                        this.getPlugin().getKitManager().getKit("NoDebuff").applyToPlayer(playerx);
                        playerx.updateInventory();
                    }
                }

                picked1.teleport(this.getGameLocations()[0].toBukkitLocation());
                picked2.teleport(this.getGameLocations()[1].toBukkitLocation());
            });
            this.sendMessage(
                new String[]{
                    ChatColor.RED
                        + "[Event] "
                        + ChatColor.GRAY.toString()
                        + "Upcoming Match: "
                        + ChatColor.RED
                        + picked1.getName()
                        + ChatColor.GRAY
                        + " vs. "
                        + ChatColor.BLUE
                        + picked2.getName()
                        + ChatColor.GRAY
                        + "."
                }
            );
        } else {
            List<UUID> winnerTeam = this.getWinningTeam();
            String winnerTeamName = ChatColor.WHITE.toString() + ChatColor.BOLD + "Tie";
            if (this.redTeam.size() > this.blueTeam.size()) {
                winnerTeamName = ChatColor.RED.toString() + ChatColor.BOLD + "RED";
            } else if (this.blueTeam.size() > this.redTeam.size()) {
                winnerTeamName = ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE";
            }

            StringJoiner winnerJoiner = new StringJoiner(", ");
            if (winnerTeam != null && winnerTeam.size() > 0) {
                for (UUID winner : winnerTeam) {
                    Player player = this.getPlugin().getServer().getPlayer(winner);
                    if (player != null) {
                        winnerJoiner.add(player.getName());
                        this.fighting.remove(player.getUniqueId());
                    }
                }
            }

            for (int i = 0; i <= 2; i++) {
                String announce = ChatColor.RED
                    + "[Event] "
                    + ChatColor.WHITE.toString()
                    + "Winner: "
                    + winnerTeamName
                    + (winnerJoiner.length() == 0 ? "" : "\n" + ChatColor.RED + "[Event] " + ChatColor.GRAY + winnerJoiner.toString());
                Bukkit.broadcastMessage(announce);
            }

            this.gameTask.cancel();
            this.end();
        }
    }

    private void generateTeams() {
        ArrayList<UUID> players = Lists.newArrayList(this.players.keySet());
        this.redTeam.addAll(players.subList(0, players.size() / 2 + players.size() % 2));
        this.blueTeam.addAll(players.subList(players.size() / 2 + players.size() % 2, players.size()));

        for (UUID uuid : this.blueTeam) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(
                    ChatColor.RED
                        + "[Event] "
                        + ChatColor.GRAY.toString()
                        + "You have been added to the "
                        + ChatColor.BLUE.toString()
                        + ChatColor.BOLD
                        + "BLUE"
                        + ChatColor.GRAY
                        + " Team."
                );
            }
        }

        for (UUID uuidx : this.redTeam) {
            Player player = Bukkit.getPlayer(uuidx);
            if (player != null) {
                player.sendMessage(
                    ChatColor.RED
                        + "[Event] "
                        + ChatColor.GRAY.toString()
                        + "You have been added to the "
                        + ChatColor.RED.toString()
                        + ChatColor.BOLD
                        + "RED"
                        + ChatColor.GRAY
                        + " Team."
                );
            }
        }
    }

    private List<UUID> getWinningTeam() {
        if (this.redTeam.size() > this.blueTeam.size()) {
            return this.redTeam;
        } else {
            return this.blueTeam.size() > this.redTeam.size() ? this.blueTeam : null;
        }
    }

    public List<UUID> getByState(RedroverPlayer.RedroverState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();
        int playingParkour = this.getByState(RedroverPlayer.RedroverState.WAITING).size() + this.getByState(RedroverPlayer.RedroverState.FIGHTING).size();
        strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Players§7: " + playingParkour + "/" + this.getLimit());
        int countdown = this.getCountdownTask().getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
        }

        if (this.getPlayer(player) != null) {
            RedroverPlayer redroverPlayer = this.getPlayer(player);
            strings.add(
                ChatColor.RED.toString()
                    + ChatColor.BOLD
                    + " * "
                    + ChatColor.WHITE
                    + "State§7: "
                    + StringUtils.capitalize(redroverPlayer.getState().name().toLowerCase())
            );
        }

        if (this.getFighting().size() > 0) {
            StringJoiner joiner = new StringJoiner(ChatColor.WHITE + " vs " + ChatColor.WHITE);

            for (UUID fighterUUID : this.getFighting()) {
                Player fighter = Bukkit.getPlayer(fighterUUID);
                if (fighter != null) {
                    joiner.add(fighter.getName());
                }
            }

            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------");
            strings.add(ChatColor.WHITE + joiner.toString());
        }

        return strings;
    }

    @Override
    public List<String> getScoreboardSpectator(Player player) {
        List<String> strings = new ArrayList<>();
        int playingParkour = this.getByState(RedroverPlayer.RedroverState.WAITING).size() + this.getByState(RedroverPlayer.RedroverState.FIGHTING).size();
        strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Players§7: " + playingParkour + "/" + this.getLimit());
        int countdown = this.getCountdownTask().getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(ChatColor.RED.toString() + ChatColor.BOLD + " * " + ChatColor.WHITE + "Starting§7: " + countdown + "s");
        }

        if (this.getFighting().size() > 0) {
            StringJoiner joiner = new StringJoiner(ChatColor.WHITE + " vs " + ChatColor.WHITE);

            for (UUID fighterUUID : this.getFighting()) {
                Player fighter = Bukkit.getPlayer(fighterUUID);
                if (fighter != null) {
                    joiner.add(fighter.getName());
                }
            }

            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-----------------");
            strings.add(ChatColor.WHITE + joiner.toString());
        }

        return strings;
    }

    public List<UUID> getBlueTeam() {
        return this.blueTeam;
    }

    public List<UUID> getRedTeam() {
        return this.redTeam;
    }

    public UUID getStreakPlayer() {
        return this.streakPlayer;
    }

    public List<UUID> getFighting() {
        return this.fighting;
    }

    public RedroverEvent.RedroverGameTask getGameTask() {
        return this.gameTask;
    }

    public class RedroverFightTask extends BukkitRunnable {
        private final Player player;
        private final Player other;
        private final RedroverPlayer redroverPlayer;
        private final RedroverPlayer redroverOther;
        private int time = 180;

        public void run() {
            if (this.player != null && this.other != null && this.player.isOnline() && this.other.isOnline()) {
                if (this.time == 180) {
                    PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", this.player, this.other);
                } else if (this.time == 179) {
                    PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", this.player, this.other);
                } else if (this.time == 178) {
                    PlayerUtil.sendMessage(ChatColor.YELLOW + "The match starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", this.player, this.other);
                } else if (this.time == 177) {
                    PlayerUtil.sendMessage(ChatColor.GREEN + "The match has started.", this.player, this.other);
                    this.redroverOther.setState(RedroverPlayer.RedroverState.FIGHTING);
                    this.redroverPlayer.setState(RedroverPlayer.RedroverState.FIGHTING);
                } else if (this.time <= 0) {
                    List<Player> players = Arrays.asList(this.player, this.other);
                    Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                    players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> RedroverEvent.this.onDeath().accept(pl));
                    this.cancel();
                    return;
                }

                if (Arrays.asList(30, 25, 20, 15, 10).contains(this.time)) {
                    PlayerUtil.sendMessage(
                        ChatColor.YELLOW + "The match ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", this.player, this.other
                    );
                } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                    PlayerUtil.sendMessage(
                        ChatColor.YELLOW + "The match is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", this.player, this.other
                    );
                }

                this.time--;
            } else {
                this.cancel();
            }
        }

        public Player getPlayer() {
            return this.player;
        }

        public Player getOther() {
            return this.other;
        }

        public RedroverPlayer getRedroverPlayer() {
            return this.redroverPlayer;
        }

        public RedroverPlayer getRedroverOther() {
            return this.redroverOther;
        }

        public int getTime() {
            return this.time;
        }

        public RedroverFightTask(Player player, Player other, RedroverPlayer redroverPlayer, RedroverPlayer redroverOther) {
            this.player = player;
            this.other = other;
            this.redroverPlayer = redroverPlayer;
            this.redroverOther = redroverOther;
        }
    }

    public class RedroverGameTask extends BukkitRunnable {
        private int time = 1200;

        public void run() {
            if (this.time == 1200) {
                RedroverEvent.this.prepareNextMatch();
            }

            if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", RedroverEvent.this.getBukkitPlayers()
                );
            } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", RedroverEvent.this.getBukkitPlayers()
                );
            }

            this.time--;
        }

        public int getTime() {
            return this.time;
        }

        public void setTime(int time) {
            this.time = time;
        }
    }
}
