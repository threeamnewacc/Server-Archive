package us.zonix.practice.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;

public class Tournament {
    private final Practice plugin = Practice.getInstance();
    private final Set<UUID> players = new HashSet<>();
    private final Set<UUID> matches = new HashSet<>();
    private final List<TournamentTeam> aliveTeams = new ArrayList<>();
    private final Map<UUID, TournamentTeam> playerTeams = new HashMap<>();
    private final int id;
    private final int teamSize;
    private final int size;
    private final String kitName;
    private TournamentState tournamentState = TournamentState.WAITING;
    private int currentRound = 1;
    private int countdown = 31;
    private int tokens = 0;

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
            player.playSound(player.getLocation(), sound, 10.0F, 1.0F);
        }
    }

    public int decrementCountdown() {
        return this.countdown <= 0 ? 0 : --this.countdown;
    }

    public Tournament(int id, int teamSize, int size, String kitName) {
        this.id = id;
        this.teamSize = teamSize;
        this.size = size;
        this.kitName = kitName;
    }

    public Set<UUID> getPlayers() {
        return this.players;
    }

    public Set<UUID> getMatches() {
        return this.matches;
    }

    public List<TournamentTeam> getAliveTeams() {
        return this.aliveTeams;
    }

    public Map<UUID, TournamentTeam> getPlayerTeams() {
        return this.playerTeams;
    }

    public int getId() {
        return this.id;
    }

    public int getTeamSize() {
        return this.teamSize;
    }

    public int getSize() {
        return this.size;
    }

    public String getKitName() {
        return this.kitName;
    }

    public TournamentState getTournamentState() {
        return this.tournamentState;
    }

    public void setTournamentState(TournamentState tournamentState) {
        this.tournamentState = tournamentState;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

    public int getCountdown() {
        return this.countdown;
    }

    public void setCountdown(int countdown) {
        this.countdown = countdown;
    }

    public int getTokens() {
        return this.tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }
}
