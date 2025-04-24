package me.devkevin.practice.tournament;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.practice.Practice;
import me.devkevin.practice.tournament.state.TournamentState;
import me.devkevin.practice.tournament.team.MatchRandomTeam;
import me.devkevin.practice.tournament.team.TournamentTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Copyright 19/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter @RequiredArgsConstructor
public class Tournament {
    private final Practice plugin = Practice.getInstance();

    private final Set<UUID> players = new HashSet<>();
    private final Set<UUID> matches = new HashSet<>();
    private final List<TournamentTeam> aliveTeams = new ArrayList<>();
    private final Map<UUID, TournamentTeam> playerTeams = new ConcurrentHashMap<>();
    @Setter private Player host;
    private final int id;
    private final int teamSize;
    private final int size;
    private final String kitName;
    @Setter private TournamentState tournamentState = TournamentState.WAITING;
    @Setter private int currentRound = 1;
    @Setter private int countdown = 31;

    public MatchRandomTeam getRandomTeam() {
        List<TournamentTeam> teams = this.getAliveTeams();
        Collections.shuffle(teams);
        TournamentTeam teamA;
        TournamentTeam teamB;
        for (int i = 0; i < teams.size(); i += 2) {
            teamA = teams.get(i);
            if (teams.size() > i + 1) {
                teamB = teams.get(i + 1);
                return new MatchRandomTeam(teamA, teamB);
            } else {
                for (UUID playerUUID : teamA.getAlivePlayers()) {
                    Player player = this.plugin.getServer().getPlayer(playerUUID);
                    player.sendMessage(CC.translate("&cYou have been skipped to the next round because there were no opponents for you this round."));
                }
            }
        }

        return null;
    }

    public void addPlayer(UUID uuid) {
        this.players.add(uuid);
    }

    public void addAliveTeam(TournamentTeam team) {
        this.aliveTeams.add(team);
    }

    public void killTeam(TournamentTeam team) {
        this.aliveTeams.remove(team);
    }

    public void setPlayerTeam(UUID uuid, TournamentTeam team) {
        this.playerTeams.put(uuid, team);
    }

    public TournamentTeam getPlayerTeam(UUID uuid) {
        return this.playerTeams.get(uuid);
    }

    public void removePlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    public void addMatch(UUID uuid) {
        this.matches.add(uuid);
    }

    public void removeMatch(UUID uuid) {
        this.matches.remove(uuid);
    }

    public boolean isTeamTournament() {
        return this.teamSize > 1;
    }

    public void broadcast(String message) {
        for (UUID uuid : this.players) {
            Player player = this.plugin.getServer().getPlayer(uuid);

            player.sendMessage(message);
        }
    }

    public void broadcastWithSound(String message, Sound sound) {
        for (UUID uuid : this.players) {
            Player player = this.plugin.getServer().getPlayer(uuid);

            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10, 1);
        }
    }

    public int decrementCountdown() {
        if (countdown <= 0) {
            return 0;
        }

        return --this.countdown;
    }
}
