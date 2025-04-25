package us.zonix.practice.match;

import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.arena.StandaloneArena;
import us.zonix.practice.inventory.InventorySnapshot;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.queue.QueueType;
import us.zonix.practice.util.Clickable;

public class Match {
    private final Practice plugin = Practice.getInstance();
    private final Map<UUID, InventorySnapshot> snapshots = new HashMap<>();
    private final Set<Entity> entitiesToRemove = new HashSet<>();
    private final Set<BlockState> originalBlockChanges = Sets.newConcurrentHashSet();
    private final Set<Location> placedBlockLocations = Sets.newConcurrentHashSet();
    private final Set<UUID> spectators = new ConcurrentSet();
    private final Set<Integer> runnables = new HashSet<>();
    private final Set<UUID> haveSpectated = new HashSet<>();
    private final List<MatchTeam> teams;
    private final UUID matchId = UUID.randomUUID();
    private final QueueType type;
    private final Arena arena;
    private final Kit kit;
    private final boolean redrover;
    private final boolean bot;
    private final boolean bestOfThree;
    private int rounds = 0;
    private StandaloneArena standaloneArena;
    private MatchState matchState = MatchState.STARTING;
    private int winningTeamId;
    private int countdown = 6;
    private Date startTime;

    public Match(Arena arena, Kit kit, QueueType type, boolean bestOfThree, MatchTeam... teams) {
        this(arena, kit, type, false, false, bestOfThree, teams);
    }

    public Match(Arena arena, Kit kit, QueueType type, boolean redrover, boolean bot, boolean bestOfThree, MatchTeam... teams) {
        this.arena = arena;
        this.kit = kit;
        this.type = type;
        this.redrover = redrover;
        this.bot = bot;
        this.teams = Arrays.asList(teams);
        this.bestOfThree = bestOfThree;
    }

    public void addSpectator(UUID uuid) {
        this.spectators.add(uuid);
    }

    public void removeSpectator(UUID uuid) {
        this.spectators.remove(uuid);
    }

    public void addHaveSpectated(UUID uuid) {
        this.haveSpectated.add(uuid);
    }

    public boolean haveSpectated(UUID uuid) {
        return this.haveSpectated.contains(uuid);
    }

    public void addSnapshot(Player player) {
        this.snapshots.put(player.getUniqueId(), new InventorySnapshot(player, this));
    }

    public boolean hasSnapshot(UUID uuid) {
        return this.snapshots.containsKey(uuid);
    }

    public InventorySnapshot getSnapshot(UUID uuid) {
        return this.snapshots.get(uuid);
    }

    public void addEntityToRemove(Entity entity) {
        this.entitiesToRemove.add(entity);
    }

    public void removeEntityToRemove(Entity entity) {
        this.entitiesToRemove.remove(entity);
    }

    public void clearEntitiesToRemove() {
        this.entitiesToRemove.clear();
    }

    public void addRunnable(int id) {
        this.runnables.add(id);
    }

    public void addOriginalBlockChange(BlockState blockState) {
        this.originalBlockChanges.add(blockState);
    }

    public void removeOriginalBlockChange(BlockState blockState) {
        this.originalBlockChanges.remove(blockState);
    }

    public void addPlacedBlockLocation(Location location) {
        this.placedBlockLocations.add(location);
    }

    public void removePlacedBlockLocation(Location location) {
        this.placedBlockLocations.remove(location);
    }

    public void broadcastWithSound(String message, Sound sound) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> {
                player.sendMessage(message);
                player.playSound(player.getLocation(), sound, 10.0F, 1.0F);
            }));
        this.spectatorPlayers().forEach(spectator -> {
            spectator.sendMessage(message);
            spectator.playSound(spectator.getLocation(), sound, 10.0F, 1.0F);
        });
    }

    public List<MatchTeam> getOtherTeam(Player player) {
        List<MatchTeam> otherTeams = new ArrayList<>();

        for (MatchTeam matchTeam : this.teams) {
            if (!matchTeam.getPlayers().contains(player.getUniqueId())) {
                otherTeams.add(matchTeam);
            }
        }

        return otherTeams;
    }

    public void broadcast(String message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendMessage(message)));
        this.spectatorPlayers().forEach(spectator -> spectator.sendMessage(message));
    }

    public void broadcast(Clickable message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(message::sendToPlayer));
        this.spectatorPlayers().forEach(message::sendToPlayer);
    }

    public Stream<Player> spectatorPlayers() {
        return this.spectators.stream().<Player>map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public int decrementCountdown() {
        return --this.countdown;
    }

    public boolean isParty() {
        return this.isFFA() || this.teams.get(0).getPlayers().size() != 1 && this.teams.get(1).getPlayers().size() != 1;
    }

    public boolean isPartyMatch() {
        return this.isFFA() || this.teams.get(0).getPlayers().size() >= 2 || this.teams.get(1).getPlayers().size() >= 2;
    }

    public boolean isFFA() {
        return this.teams.size() == 1;
    }

    public void setStandaloneArena(StandaloneArena standaloneArena) {
        this.standaloneArena = standaloneArena;
    }

    public void setMatchState(MatchState matchState) {
        this.matchState = matchState;
    }

    public void setWinningTeamId(int winningTeamId) {
        this.winningTeamId = winningTeamId;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Map<UUID, InventorySnapshot> getSnapshots() {
        return this.snapshots;
    }

    public Set<Entity> getEntitiesToRemove() {
        return this.entitiesToRemove;
    }

    public Set<BlockState> getOriginalBlockChanges() {
        return this.originalBlockChanges;
    }

    public Set<Location> getPlacedBlockLocations() {
        return this.placedBlockLocations;
    }

    public Set<UUID> getSpectators() {
        return this.spectators;
    }

    public Set<Integer> getRunnables() {
        return this.runnables;
    }

    public List<MatchTeam> getTeams() {
        return this.teams;
    }

    public UUID getMatchId() {
        return this.matchId;
    }

    public QueueType getType() {
        return this.type;
    }

    public Arena getArena() {
        return this.arena;
    }

    public Kit getKit() {
        return this.kit;
    }

    public boolean isRedrover() {
        return this.redrover;
    }

    public boolean isBot() {
        return this.bot;
    }

    public boolean isBestOfThree() {
        return this.bestOfThree;
    }

    public int getRounds() {
        return this.rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public StandaloneArena getStandaloneArena() {
        return this.standaloneArena;
    }

    public MatchState getMatchState() {
        return this.matchState;
    }

    public int getWinningTeamId() {
        return this.winningTeamId;
    }

    public int getCountdown() {
        return this.countdown;
    }

    public Date getStartTime() {
        return this.startTime;
    }
}
