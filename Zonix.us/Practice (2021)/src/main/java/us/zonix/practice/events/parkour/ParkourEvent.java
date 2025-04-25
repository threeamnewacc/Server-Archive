package us.zonix.practice.events.parkour;

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
import us.zonix.practice.util.ItemUtil;
import us.zonix.practice.util.PlayerUtil;

public class ParkourEvent extends PracticeEvent<ParkourPlayer> {
    private final Map<UUID, ParkourPlayer> players = new HashMap<>();
    private ParkourEvent.ParkourGameTask gameTask = null;
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);
    private List<UUID> visibility;

    public ParkourEvent() {
        super("Parkour", ItemUtil.createItem(Material.LADDER, ChatColor.RED + "Parkour Event"), true);
    }

    @Override
    public Map<UUID, ParkourPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getParkourLocation());
    }

    @Override
    public void onStart() {
        this.gameTask = new ParkourEvent.ParkourGameTask();
        this.gameTask.runTaskTimerAsynchronously(this.getPlugin(), 0L, 20L);
        this.visibility = new ArrayList<>();
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new ParkourPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            String message = ChatColor.RED + "[Event] " + ChatColor.RED + player.getName() + ChatColor.GRAY + " has left the game.";
            this.sendMessage(new String[]{message});
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

    public void teleportToSpawnOrCheckpoint(final Player player) {
        ParkourPlayer parkourPlayer = this.getPlayer(player.getUniqueId());
        if (parkourPlayer != null && parkourPlayer.getLastCheckpoint() != null) {
            player.teleport(parkourPlayer.getLastCheckpoint().toBukkitLocation());
            player.sendMessage(ChatColor.RED + "[Event] " + ChatColor.GRAY + "Teleporting back to last checkpoint.");
        } else {
            player.sendMessage(ChatColor.RED + "[Event] " + ChatColor.GRAY + "Teleporting back to the beginning.");
            this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    player.teleport(ParkourEvent.this.getPlugin().getSpawnManager().getParkourGameLocation().toBukkitLocation());
                }
            });
        }
    }

    private void giveItems(Player player) {
        this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.clearPlayer(player);
            player.getInventory().setItem(0, ItemUtil.createItem(Material.FIREBALL, ChatColor.GREEN.toString() + "Toggle Visibility"));
            player.getInventory().setItem(4, ItemUtil.createItem(Material.NETHER_STAR, ChatColor.RED.toString() + "Leave Event"));
            player.updateInventory();
        });
    }

    private Player getRandomPlayer() {
        if (this.getByState(ParkourPlayer.ParkourState.INGAME).size() == 0) {
            return null;
        } else {
            List<UUID> fighting = this.getByState(ParkourPlayer.ParkourState.INGAME);
            Collections.shuffle(fighting);
            UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));
            return this.getPlugin().getServer().getPlayer(uuid);
        }
    }

    public List<UUID> getByState(ParkourPlayer.ParkourState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> strings = new ArrayList<>();
        int playingParkour = this.getByState(ParkourPlayer.ParkourState.WAITING).size() + this.getByState(ParkourPlayer.ParkourState.INGAME).size();
        strings.add(" &c* &fPlayers§7: " + playingParkour + "/" + this.getLimit());
        int countdown = this.countdownTask.getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(" &c* &fStarting§7: " + countdown + "s");
        }

        if (this.getPlayer(player) != null) {
            ParkourPlayer parkourPlayer = this.getPlayer(player);
            if (parkourPlayer.getLastCheckpoint() != null && parkourPlayer.getCheckpointId() > 0) {
                strings.add(" &c* &fCheckpoint§7: §aSaved");
            }
        }

        return strings;
    }

    public ParkourEvent.ParkourGameTask getGameTask() {
        return this.gameTask;
    }

    public class ParkourGameTask extends BukkitRunnable {
        private int time = 303;

        public void run() {
            if (this.time == 303) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 302) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 301) {
                PlayerUtil.sendMessage(
                    ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers()
                );
            } else if (this.time == 300) {
                PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", ParkourEvent.this.getBukkitPlayers());

                for (ParkourPlayer player : ParkourEvent.this.getPlayers().values()) {
                    player.setLastCheckpoint(null);
                    player.setState(ParkourPlayer.ParkourState.INGAME);
                    player.setCheckpointId(0);
                }

                for (Player player : ParkourEvent.this.getBukkitPlayers()) {
                    ParkourEvent.this.teleportToSpawnOrCheckpoint(player);
                    ParkourEvent.this.giveItems(player);
                }
            } else if (this.time <= 0) {
                Player winner = ParkourEvent.this.getRandomPlayer();
                if (winner != null) {
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setParkourEventWins(winnerData.getParkourEventWins() + 1);
                    Profile winnerProfile = Profile.getByUuid(winner.getUniqueId());
                    winnerProfile.awardCoins(winner, 15);
                    winner.sendMessage(CC.GOLD + "You earn 15 coins for winning the event!");

                    for (int i = 0; i <= 2; i++) {
                        String announce = ChatColor.RED + winner.getName() + ChatColor.WHITE + " has won the event!";
                        Bukkit.broadcastMessage(announce);
                    }

                    String announce = ChatColor.DARK_RED
                        + winner.getName()
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + "Parkour"
                        + ChatColor.WHITE
                        + " event!";
                    Bukkit.broadcastMessage(announce);
                }

                ParkourEvent.this.end();
                this.cancel();
                return;
            }

            if (ParkourEvent.this.getPlayers().size() != 1) {
                if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(this.time)) {
                    PlayerUtil.sendMessage(
                        ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...", ParkourEvent.this.getBukkitPlayers()
                    );
                } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                    PlayerUtil.sendMessage(
                        ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + this.time + ChatColor.YELLOW + "...",
                        ParkourEvent.this.getBukkitPlayers()
                    );
                }

                this.time--;
            } else {
                Player winner = Bukkit.getPlayer(ParkourEvent.this.getByState(ParkourPlayer.ParkourState.INGAME).get(0));
                if (winner != null) {
                    PlayerData winnerData = Practice.getInstance().getPlayerManager().getPlayerData(winner.getUniqueId());
                    winnerData.setParkourEventWins(winnerData.getParkourEventWins() + 1);
                    Profile winnerProfile = Profile.getByUuid(winner.getUniqueId());
                    winnerProfile.awardCoins(winner, 15);
                    winner.sendMessage(CC.GOLD + "You earn 15 coins for winning the event!");

                    for (int i = 0; i <= 2; i++) {
                        String announce = ChatColor.RED + winner.getName() + ChatColor.WHITE + " has won the event!";
                        Bukkit.broadcastMessage(announce);
                    }

                    String announce = ChatColor.DARK_RED
                        + winner.getName()
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + "Parkour"
                        + ChatColor.WHITE
                        + " event!";
                    Bukkit.broadcastMessage(announce);
                }

                ParkourEvent.this.end();
                this.cancel();
            }
        }

        public int getTime() {
            return this.time;
        }
    }
}
