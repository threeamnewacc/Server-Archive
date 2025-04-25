package us.zonix.practice.events.tnttag;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import me.maiko.dexter.profile.Profile;
import me.maiko.dexter.util.CC;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.CustomLocation;
import us.zonix.practice.Practice;
import us.zonix.practice.events.EventCountdownTask;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.EventState;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.util.ItemBuilder;

public class TNTTagEvent extends PracticeEvent<TNTTagPlayer> {
    private final Map<UUID, TNTTagPlayer> players = Maps.newHashMap();
    private final List<CustomLocation> spawnLocations = Lists.newArrayList();
    private final EventCountdownTask countdownTask = new EventCountdownTask(this);
    private TNTTagEvent.TNTTagState tntTagState = TNTTagEvent.TNTTagState.NOT_STARTED;
    private TNTTagEvent.TNTTagTask task;
    private int round = 0;

    public TNTTagEvent() {
        super("TNTTag", new ItemBuilder(Material.TNT).name("&cTNT Tag event").build(), true);
    }

    @Override
    public void onStart() {
        this.tntTagState = TNTTagEvent.TNTTagState.PREPARING;
        this.task = new TNTTagEvent.TNTTagTask(this);
        this.round = 0;
        this.task.runTaskTimer(Practice.getInstance(), 20L, 20L);

        for (Player bukkitPlayer : this.getBukkitPlayers()) {
            bukkitPlayer.teleport(Practice.getInstance().getSpawnManager().getTntTagSpawn().toBukkitLocation());
        }
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> this.players.put(player.getUniqueId(), new TNTTagPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {
            if (this.getState() == EventState.STARTED) {
                boolean wasTagged = this.getPlayer(player).isTagged();
                this.players.remove(player.getUniqueId());
                List<TNTTagPlayer> tntTagPlayers = this.getPlayers()
                    .values()
                    .stream()
                    .filter(EventPlayer::playerExists)
                    .filter(TNTTagPlayer::isTagged)
                    .collect(Collectors.toList());
                if (wasTagged && tntTagPlayers.size() == 0) {
                    this.setTntTagState(TNTTagEvent.TNTTagState.PREPARING);
                    this.task.time = 5;
                }

                this.sendMessage(new String[]{"&c" + player.getName() + " &ehas exploded!"});
                player.sendMessage(ChatColor.RED + "You have exploded!");
                Profile profile = Profile.getByUuid(player.getUniqueId());
                profile.awardCoins(player, 5);
                player.sendMessage(CC.GOLD + "You earn 5 coins for participating in the event.");
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
                                    .addSpectatorTntTag(player, this.getPlugin().getPlayerManager().getPlayerData(player.getUniqueId()), this);
                            }
                        },
                        20L
                    );
                if (this.players.size() == 1) {
                    Player winner = this.players.values().stream().findFirst().get().getPlayer();
                    String announce = ChatColor.DARK_RED + winner.getName() + ChatColor.WHITE + " has won our " + ChatColor.DARK_RED + "TNT Tag Event!";
                    Bukkit.broadcastMessage(announce);
                    Profile winnerProfile = Profile.getByUuid(winner.getUniqueId());
                    winnerProfile.awardCoins(winner, 15);
                    winner.sendMessage(CC.GOLD + "You earn 15 coins for winning the event!");
                    Bukkit.broadcastMessage(announce);
                    this.end();
                }
            }
        };
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getTntTagLocation());
    }

    public List<CustomLocation> getGameLocation() {
        return Collections.singletonList(this.getPlugin().getSpawnManager().getTntTagLocation());
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> strings = Lists.newArrayList();
        strings.add(" &c* &fPlayers&7: " + this.players.size() + "/" + this.getLimit());
        int countdown = this.countdownTask.getTimeUntilStart();
        if (countdown > 0 && countdown <= 60) {
            strings.add(" &c* &fStarting&7: " + countdown + "s");
        }

        if (this.round != 0) {
            strings.add(" &c* &fRound&7: " + this.round);
        }

        if (this.tntTagState == TNTTagEvent.TNTTagState.PREPARING && this.task.time <= 3) {
            strings.add(" &c* &fNext Round&7: " + this.task.getTime() + "s");
        } else if (this.tntTagState == TNTTagEvent.TNTTagState.RUNNING) {
            strings.add(" &c* &fTime Left&7: " + this.task.getTime() + "s");
        }

        return strings;
    }

    @Override
    public Map<UUID, TNTTagPlayer> getPlayers() {
        return this.players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return this.countdownTask;
    }

    public TNTTagEvent.TNTTagState getTntTagState() {
        return this.tntTagState;
    }

    public TNTTagEvent.TNTTagTask getTask() {
        return this.task;
    }

    public int getRound() {
        return this.round;
    }

    public void setTntTagState(TNTTagEvent.TNTTagState tntTagState) {
        this.tntTagState = tntTagState;
    }

    public void setTask(TNTTagEvent.TNTTagTask task) {
        this.task = task;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public static enum TNTTagState {
        RUNNING,
        PREPARING,
        NOT_STARTED;
    }

    public static class TNTTagTask extends BukkitRunnable {
        public static final Map<Integer, Integer> TIME_MAP = Maps.newHashMap();
        public static final List<Integer> BROADCAST_TIMES = Lists.newArrayList();
        public static final int BLOW_UP_RADIUS = 1;
        public final TNTTagEvent event;
        public int previousTime = 90;
        public int time = 3;

        public void run() {
            if (this.event.getTntTagState() != TNTTagEvent.TNTTagState.NOT_STARTED) {
                if (this.event.getTntTagState() != TNTTagEvent.TNTTagState.PREPARING) {
                    if (BROADCAST_TIMES.contains(this.time)) {
                        this.event
                            .sendMessage(
                                new String[]{
                                    "&eAll tagged players will explode in &c"
                                        + DurationFormatUtils.formatDurationWords((long)(this.time * 1000), true, true)
                                        + "&e."
                                }
                            );
                    }

                    if (this.time == 0) {
                        List<TNTTagPlayer> tagged = this.event.getPlayers().values().stream().filter(TNTTagPlayer::isTagged).collect(Collectors.toList());
                        Consumer<Player> deathConsumer = this.event.onDeath();
                        this.event.sendMessage(new String[]{"&c" + tagged.size() + " &eplayers have been removed from the game."});
                        this.event.setTntTagState(TNTTagEvent.TNTTagState.PREPARING);
                        this.time = 5;
                        tagged.stream()
                            .filter(EventPlayer::playerExists)
                            .forEach(
                                player -> {
                                    Player bukkitPlayer = player.getPlayer();
                                    bukkitPlayer.getWorld().playEffect(bukkitPlayer.getLocation(), Effect.EXPLOSION_LARGE, 1, 1);
                                    bukkitPlayer.getWorld().playSound(bukkitPlayer.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                                    deathConsumer.accept(bukkitPlayer);
                                    if (this.event.players.size() > 1) {
                                        bukkitPlayer.getNearbyEntities(1.0, 1.0, 1.0)
                                            .stream()
                                            .filter(entity -> this.event.getPlayers().containsKey(entity.getUniqueId()))
                                            .map(entity -> this.event.getPlayers().get(entity.getUniqueId()))
                                            .forEach(nearby -> {
                                                Player nearbyPlayer = nearby.getPlayer();
                                                nearbyPlayer.getWorld().playEffect(nearbyPlayer.getLocation(), Effect.EXPLOSION_LARGE, 1, 1);
                                                nearbyPlayer.getWorld().playSound(nearbyPlayer.getLocation(), Sound.EXPLODE, 1.0F, 1.0F);
                                                deathConsumer.accept(nearbyPlayer);
                                                if (this.event.players.size() > 1) {
                                                    nearbyPlayer.sendMessage(ChatColor.RED + "You died because you were to close to another tagged player.");
                                                }
                                            });
                                    }
                                }
                            );
                    }

                    this.time--;
                } else if (this.time != 0) {
                    this.event.sendMessage(new String[]{"&eRound &c" + (this.event.getRound() + 1) + " &ewill start in &a" + this.time + " &eseconds."});
                    this.time--;
                } else {
                    List<TNTTagPlayer> players = this.event
                        .getPlayers()
                        .values()
                        .stream()
                        .filter(EventPlayer::playerExists)
                        .filter(player -> !player.isTagged())
                        .collect(Collectors.toList());
                    int size = (int)Math.round((double)players.size() / 2.5);
                    this.event.setRound(this.event.getRound() + 1);
                    this.event
                        .sendMessage(new String[]{"&eRound &c" + this.event.getRound() + " &ehas started!", "&f" + size + " &7players have been tagged!"});
                    Collections.shuffle(players);

                    for (int i = 0; i < players.size() && i < size; i++) {
                        TNTTagPlayer tagPlayer = players.get(i);
                        tagPlayer.setTagged(true);
                    }

                    players.forEach(player -> {
                        player.update();
                        if (this.event.getRound() > 1 && this.event.getPlayers().size() <= 8) {
                            player.getPlayer().teleport(Practice.getInstance().getSpawnManager().getTntTagSpawn().toBukkitLocation());
                        }
                    });
                    this.event.setTntTagState(TNTTagEvent.TNTTagState.RUNNING);
                    this.time = TIME_MAP.getOrDefault(this.previousTime, 5);
                    this.previousTime = this.time;
                }
            }
        }

        public TNTTagEvent getEvent() {
            return this.event;
        }

        public int getPreviousTime() {
            return this.previousTime;
        }

        public int getTime() {
            return this.time;
        }

        public TNTTagTask(TNTTagEvent event) {
            this.event = event;
        }

        static {
            TIME_MAP.put(90, 60);
            TIME_MAP.put(60, 50);
            TIME_MAP.put(50, 30);
            TIME_MAP.put(30, 20);
            TIME_MAP.put(20, 15);
            TIME_MAP.put(15, 10);
            TIME_MAP.put(10, 5);
            TIME_MAP.put(5, 5);
            BROADCAST_TIMES.addAll(Arrays.asList(60, 30, 20, 15, 10, 5, 4, 3, 2, 1));
        }
    }
}
