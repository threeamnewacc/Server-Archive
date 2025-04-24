package me.devkevin.practice.match.team;

import lombok.Data;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Copyright 28/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Data
public class KillableTeam {

    protected final Practice plugin = Practice.getInstance();

    private final List<UUID> players;
    private final List<UUID> alivePlayers = new ArrayList<>();
    private final String leaderName;
    @Setter private UUID leader;

    public KillableTeam(UUID leader, List<UUID> players) {
        this.leader = leader;
        this.leaderName = this.plugin.getServer().getPlayer(leader).getName();
        this.players = players;
        this.alivePlayers.addAll(players);
    }

    public void killPlayer(UUID playerUUID) {
        this.alivePlayers.remove(playerUUID);
    }

    public Stream<Player> alivePlayers() {
        return this.alivePlayers.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public Stream<Player> players() {
        return this.players.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
    }

    public List<Player> getPlayersConverted() {
        List<Player> newPlayerList = new ArrayList<>();

        for (UUID uuid : this.players) {
            if (Bukkit.getPlayer(uuid) != null) {
                Player player = Bukkit.getPlayer(uuid);

                newPlayerList.add(player);
            }
        }

        return newPlayerList;
    }

    public int onlinePlayers() {
        int count = 0;
        for (UUID uuid : players) {
            Profile profile = this.plugin.getProfileManager().getProfileData(uuid);

            if (profile != null && !profile.isLeaving()) {
                count++;
            }
        }
        return count;
    }
}
