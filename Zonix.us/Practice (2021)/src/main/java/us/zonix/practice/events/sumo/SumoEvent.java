package us.zonix.practice.events.sumo;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

public class SumoEvent extends PracticeEvent<SumoPlayer> {
    private final Map<UUID, SumoPlayer> players = new HashMap<>();
    final HashSet<String> fighting = new HashSet<>();
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);
    int round;

    public SumoEvent() {
        super("Sumo", ItemUtil.createItem(Material.LEASH, ChatColor.RED + "Sumo Event"), true);
    }

    @Override
    public Map<UUID, SumoPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getSumoLocation());
    }

    @Override
    public void onStart() {
        this.selectPlayers();

        for (UUID playerUUID : this.players.keySet()) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null) {
                PlayerUtil.clearPlayer(player);
            }
        }
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new SumoPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            SumoPlayer data = this.getPlayer(player);
            if (data != null && data.getFighting() != null) {
                if (data.getState() == SumoPlayer.SumoState.FIGHTING || data.getState() == SumoPlayer.SumoState.PREPARING) {
                    SumoPlayer killerData = data.getFighting();
                    Player killer = this.getPlugin().getServer().getPlayer(killerData.getUuid());
                    data.getFightTask().cancel();
                    killerData.getFightTask().cancel();
                    data.setState(SumoPlayer.SumoState.ELIMINATED);
                    killerData.setState(SumoPlayer.SumoState.WAITING);
                    PlayerUtil.clearPlayer(player);
                    this.getPlugin().getPlayerManager().giveLobbyItems(player);
                    PlayerUtil.clearPlayer(killer);
                    this.getPlugin().getPlayerManager().giveLobbyItems(killer);
                    if (this.getSpawnLocations().size() == 1) {
                        player.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
                        killer.teleport(this.getSpawnLocations().get(0).toBukkitLocation());
                    }

                    this.sendMessage(
                        new String[]{
                            ChatColor.RED
                                + "[Event] "
                                + ChatColor.RED
                                + player.getName()
                                + ChatColor.GRAY
                                + " has been eliminated"
                                + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName())
                        }
                    );
                    if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
                        Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).stream().findFirst().get());
                        if (winner != null) {
                            String announce = ChatColor.DARK_RED
                                + winner.getName()
                                + ChatColor.WHITE
                                + " has won our "
                                + ChatColor.DARK_RED
                                + "Sumo"
                                + ChatColor.WHITE
                                + " event!";
                            Bukkit.broadcastMessage(announce);
                        }

                        this.fighting.clear();
                        this.end();
                    } else {
                        this.getPlugin().getServer().getScheduler().runTaskLater(this.getPlugin(), () -> this.selectPlayers(), 60L);
                    }
                }
            } else {
                System.out.println("data is null");
            }
        };
    }

    private CustomLocation[] getSumoLocations() {
        return new CustomLocation[]{this.getPlugin().getSpawnManager().getSumoFirst(), this.getPlugin().getSpawnManager().getSumoSecond()};
    }

    private void selectPlayers() {
        if (this.getByState(SumoPlayer.SumoState.WAITING).size() == 1) {
            Player winner = Bukkit.getPlayer(this.getByState(SumoPlayer.SumoState.WAITING).get(0));
            String announce = ChatColor.DARK_RED
                + winner.getName()
                + ChatColor.WHITE
                + " has won our "
                + ChatColor.DARK_RED
                + "Sumo"
                + ChatColor.WHITE
                + " event!";
            Bukkit.broadcastMessage(announce);
            this.fighting.clear();
            this.end();
        } else {
            this.sendMessage(new String[]{ChatColor.RED + "[Event] " + ChatColor.GRAY + "Selecting random players..."});
            this.fighting.clear();
            Player picked1 = this.getRandomPlayer();
            Player picked2 = this.getRandomPlayer();
            SumoPlayer picked1Data = this.getPlayer(picked1);
            SumoPlayer picked2Data = this.getPlayer(picked2);
            picked1Data.setFighting(picked2Data);
            picked2Data.setFighting(picked1Data);
            this.fighting.add(picked1.getName());
            this.fighting.add(picked2.getName());
            PlayerUtil.clearPlayer(picked1);
            PlayerUtil.clearPlayer(picked2);
            picked1.teleport(this.getSumoLocations()[0].toBukkitLocation());
            picked2.teleport(this.getSumoLocations()[1].toBukkitLocation());

            for (Player other : this.getBukkitPlayers()) {
                if (other != null) {
                    other.showPlayer(picked1);
                    other.showPlayer(picked2);
                }
            }

            for (UUID spectatorUUID : this.getPlugin().getEventManager().getSpectators().keySet()) {
                Player spectator = Bukkit.getPlayer(spectatorUUID);
                if (spectatorUUID != null) {
                    spectator.showPlayer(picked1);
                    spectator.showPlayer(picked2);
                }
            }

            picked1.showPlayer(picked2);
            picked2.showPlayer(picked1);
            this.sendMessage(
                new String[]{ChatColor.YELLOW + "Starting event match. " + ChatColor.GREEN + "(" + picked1.getName() + " vs " + picked2.getName() + ")"}
            );
            this.round++;
            BukkitTask task = new SumoEvent.SumoFightTask(picked1, picked2, picked1Data, picked2Data).runTaskTimer(this.getPlugin(), 0L, 20L);
            picked1Data.setFightTask(task);
            picked2Data.setFightTask(task);
        }
    }

    private Player getRandomPlayer() {
        List<UUID> waiting = this.getByState(SumoPlayer.SumoState.WAITING);
        Collections.shuffle(waiting);
        UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));
        SumoPlayer data = this.getPlayer(uuid);
        data.setState(SumoPlayer.SumoState.PREPARING);
        return this.getPlugin().getServer().getPlayer(uuid);
    }

    public List<UUID> getByState(SumoPlayer.SumoState state) {
        return this.players.values().stream().filter(player -> player.getState() == state).map(EventPlayer::getUuid).collect(Collectors.toList());
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> strings = Lists.newArrayList();
        strings.add(" &c* &fPlayers&7: " + this.players.size() + "/" + this.getLimit());
        int countdown = this.countdownTask.getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(" &c* &fStarting&7: " + countdown + "s");
        }

        if (this.getPlayer(player) != null) {
            SumoPlayer sumoPlayer = this.getPlayer(player);
            strings.add(" &c* &fState&7: " + StringUtils.capitalize(sumoPlayer.getState().name().toLowerCase()));
        }

        if (this.getFighting().size() > 0) {
            StringJoiner nameJoiner = new StringJoiner(ChatColor.WHITE + " vs " + ChatColor.WHITE);
            StringJoiner pingJoiner = new StringJoiner(" ┃ ");
            StringJoiner cpsJoiner = new StringJoiner(" ┃ ");

            for (String fighterName : this.getFighting()) {
                nameJoiner.add("&f" + fighterName);
                Player fighter = Bukkit.getPlayer(fighterName);
                if (fighter != null) {
                    pingJoiner.add(ChatColor.GRAY + "(" + ChatColor.RED + PlayerUtil.getPing(fighter) + "ms" + ChatColor.GRAY + ")");
                    cpsJoiner.add(
                        ChatColor.GRAY
                            + "("
                            + ChatColor.RED
                            + this.getPlugin().getPlayerManager().getPlayerData(fighter.getUniqueId()).getCps()
                            + "CPS"
                            + ChatColor.GRAY
                            + ")"
                    );
                }
            }

            strings.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------");
            strings.add(ChatColor.RED + nameJoiner.toString());
            strings.add(pingJoiner.toString());
            strings.add(cpsJoiner.toString());
        }

        return strings;
    }

    public HashSet<String> getFighting() {
        return this.fighting;
    }

    public int getRound() {
        return this.round;
    }

    public class SumoFightTask extends BukkitRunnable {
        private final Player player;
        private final Player other;
        private final SumoPlayer playerSumo;
        private final SumoPlayer otherSumo;
        private int time = 90;

        public void run() {
            if (this.player != null && this.other != null && this.player.isOnline() && this.other.isOnline()) {
                if (this.time == 90) {
                    SumoEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + SumoEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GOLD
                                + "The match starts in "
                                + ChatColor.GREEN
                                + 3
                                + ChatColor.YELLOW
                                + "..."
                        }
                    );
                } else if (this.time == 89) {
                    SumoEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + SumoEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GOLD
                                + "The match starts in "
                                + ChatColor.GREEN
                                + 2
                                + ChatColor.YELLOW
                                + "..."
                        }
                    );
                } else if (this.time == 88) {
                    SumoEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + SumoEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GOLD
                                + "The match starts in "
                                + ChatColor.GREEN
                                + 1
                                + ChatColor.YELLOW
                                + "..."
                        }
                    );
                } else if (this.time == 87) {
                    SumoEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + SumoEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GOLD
                                + "The match has started."
                        }
                    );
                    SumoEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + SumoEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GOLD
                                + this.player.getName()
                                + ChatColor.YELLOW
                                + " vs "
                                + ChatColor.GOLD
                                + this.other.getName()
                        }
                    );
                    this.otherSumo.setState(SumoPlayer.SumoState.FIGHTING);
                    this.playerSumo.setState(SumoPlayer.SumoState.FIGHTING);
                } else if (this.time <= 0) {
                    List<Player> players = Arrays.asList(this.player, this.other);
                    Player winner = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                    players.stream().filter(pl -> !pl.equals(winner)).forEach(pl -> SumoEvent.this.onDeath().accept(pl));
                    this.cancel();
                    return;
                }

                if (Arrays.asList(30, 25, 20, 15, 10).contains(this.time)) {
                    SumoEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + SumoEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GOLD
                                + "The match ends in "
                                + ChatColor.GREEN
                                + this.time
                                + ChatColor.YELLOW
                                + "..."
                        }
                    );
                } else if (Arrays.asList(5, 4, 3, 2, 1).contains(this.time)) {
                    SumoEvent.this.sendMessage(
                        new String[]{
                            ChatColor.GRAY
                                + "["
                                + ChatColor.YELLOW
                                + "Round "
                                + SumoEvent.this.round
                                + ChatColor.GRAY
                                + "] "
                                + ChatColor.GOLD
                                + "The match is ending in "
                                + ChatColor.GREEN
                                + this.time
                                + ChatColor.YELLOW
                                + "..."
                        }
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

        public SumoPlayer getPlayerSumo() {
            return this.playerSumo;
        }

        public SumoPlayer getOtherSumo() {
            return this.otherSumo;
        }

        public int getTime() {
            return this.time;
        }

        public SumoFightTask(Player player, Player other, SumoPlayer playerSumo, SumoPlayer otherSumo) {
            this.player = player;
            this.other = other;
            this.playerSumo = playerSumo;
            this.otherSumo = otherSumo;
        }
    }
}
