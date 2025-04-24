package me.devkevin.practice.match;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.arena.Arena;
import me.devkevin.practice.arena.standalone.StandaloneArena;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.menu.MatchDetailsMenu;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.util.Clickable;
import me.devkevin.practice.util.TimeUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.util.*;
import java.util.stream.Stream;

/**
 * Copyright 27/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
@Setter
public class Match {

    private final Practice plugin = Practice.getInstance();

    @Getter private final Map<UUID, MatchDetailsMenu> snapshots = new HashMap<>();

    private static List<Match> matches = new ArrayList<>();

    private final Set<Entity> entitiesToRemove = new HashSet<>();
    private final LinkedList<BlockState> originalBlockChanges = Lists.newLinkedList();
    private final Set<Location> placedBlockLocations = Sets.newConcurrentHashSet();
    private final Set<UUID> spectators = new ConcurrentSet<>();
    private final Set<Integer> runnables = new HashSet<>();
    private final Set<UUID> haveSpectated = new HashSet<>();

    private final List<MatchTeam> teams;

    private final UUID matchId = UUID.randomUUID();
    private final QueueType type;
    private final Kit kit;
    private final Arena arena;

    private StandaloneArena standaloneArena;
    private MatchState matchState = MatchState.STARTING;
    private int countdown = 6;
    private int winningTeamId;

    private long startTimestamp = -1;

    private boolean redrover;
    private boolean canContinue = true;

    private String info = "info";

    private int durationTimer;

    public Match(Arena arena, Kit kit, QueueType type, MatchTeam... teams) {
        this.arena = arena;
        this.kit = kit;
        this.type = type;
        this.teams = Arrays.asList(teams);
    }

    public boolean isStarting() {
        return matchState == MatchState.STARTING;
    }

    public boolean isFighting() {
        return matchState == MatchState.FIGHTING;
    }

    public boolean isEnding() {
        return matchState == MatchState.ENDING;
    }

    public String getDuration() {
        if (this.isStarting()) {
            return CC.R + "0:00";
        }
        if (this.isEnding()) {
            return CC.R + TimeUtils.formatIntoMMSS(durationTimer);
        }
        if (isFighting()) {
            return CC.R + TimeUtils.formatIntoMMSS(durationTimer);
        }
        return null;
    }

    public String getInfoAnimation() {
        switch (info) {
            case "info":
                info = "info.";
                break;
            case "info.":
                info = "info..";
                break;
            case "info..":
                info = "info...";
                break;
            default:
                info = "info";
                break;
        }
        return info;
    }

    public long getElapsedDuration() {
        return System.currentTimeMillis() - startTimestamp;
    }

    public void broadcastWithSound(String message, Sound sound) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> {
            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10, 1);
        }));
        this.spectatorPlayers().forEach(spectator -> {
            spectator.sendMessage(message);
            spectator.playSound(spectator.getLocation(), sound, 10, 1);
        });
    }

    public void broadcastTitle(String message, String subMessage) {
        Title title = new Title(message, subMessage, 1, 20, 0);

        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendTitle(title)));
        this.spectatorPlayers().forEach(spectator -> spectator.sendTitle(title));
    }

    public void broadcastSound(Sound sound) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.playSound(player.getLocation(), sound, 10, 1)));
        this.spectatorPlayers().forEach(spectator -> spectator.playSound(spectator.getLocation(), sound, 10, 1));
    }

    public void broadcastMessage(String message) {
        teams.forEach(team -> team.alivePlayers().forEach(player -> player.sendMessage(message)));
        spectatorPlayers().forEach(spectator -> spectator.sendMessage(message));
    }

    public void broadcastMessage(Clickable message) {
        teams.forEach(team -> team.alivePlayers().forEach(message::sendToPlayer));
        spectatorPlayers().forEach(message::sendToPlayer);
    }

    public void playSound(Sound sound, Location location, float idk2) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> {
            player.playSound(player.getLocation(), sound, 10, 1);
        }));

        this.spectatorPlayers().forEach(spectator -> spectator.playSound(location, sound, 10.0F, idk2));
    }

    public MatchTeam getTeamById(int id) {
        return this.getTeams().stream().filter(matchTeam -> matchTeam.getTeamID() == id).findFirst().orElse(null);
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
        this.snapshots.put(player.getUniqueId(), new MatchDetailsMenu(player, this));
    }

    public boolean hasSnapshot(UUID uuid) {
        return this.snapshots.containsKey(uuid);
    }

    public MatchDetailsMenu getSnapshot(UUID uuid) {
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

    public Stream<Player> spectatorPlayers() {
        return this.spectators.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public int decrementCountdown() {
        return --this.countdown;
    }

    public void incrementDuration() {
        ++this.durationTimer;
    }

    public boolean isFFA() {
        return this.teams.size() == 1;
    }

    public boolean isParty() {
        return this.isFFA() || this.teams.get(0).getPlayers().size() != 1 && this.teams.get(1).getPlayers().size() != 1;
    }

    public boolean isPartyMatch() {
        return this.isFFA() || (this.teams.get(0).getPlayers().size() >= 2 || this.teams.get(1).getPlayers().size() >= 2);
    }

    public MatchTeam getMatchTeam(int id) {
        for (MatchTeam matchTeam : this.getTeams()) {
            if (matchTeam.getTeamID() == id) {
                return matchTeam;
            }
        }
        return null;
    }

    public MatchTeam getMatchTeam(Player player) {
        for (MatchTeam matchTeam : this.getTeams()) {
            if (matchTeam.getPlayers().contains(player.getUniqueId())) {
                return matchTeam;
            }
        }
        return null;
    }

    public 	List<MatchTeam> getOtherTeam(Player player) {

        List<MatchTeam> otherTeams = new ArrayList<>();

        for(MatchTeam matchTeam : this.teams) {
            if(matchTeam.getPlayers().contains(player.getUniqueId())) {
                continue;
            }
            otherTeams.add(matchTeam);
        }
        return otherTeams;

    }

    public void broadcastMessage(BaseComponent[] message) {
        this.teams.forEach(team -> team.alivePlayers().forEach(player -> player.spigot().sendMessage(message)));
        this.spectatorPlayers().forEach(spectator -> spectator.spigot().sendMessage(message));
    }

}
