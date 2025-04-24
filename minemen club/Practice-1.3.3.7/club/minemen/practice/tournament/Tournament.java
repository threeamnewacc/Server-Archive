// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.tournament;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.HashSet;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import club.minemen.practice.Practice;

public class Tournament
{
    private final Practice plugin;
    private final Set<UUID> players;
    private final Set<UUID> matches;
    private final List<TournamentTeam> aliveTeams;
    private final Map<UUID, TournamentTeam> playerTeams;
    private final int id;
    private final int teamSize;
    private final int size;
    private final String kitName;
    private TournamentState tournamentState;
    private int currentRound;
    private int countdown;
    
    public void addPlayer(final UUID uuid) {
        this.players.add(uuid);
    }
    
    public void addAliveTeam(final TournamentTeam team) {
        this.aliveTeams.add(team);
    }
    
    public void killTeam(final TournamentTeam team) {
        this.aliveTeams.remove(team);
    }
    
    public void setPlayerTeam(final UUID uuid, final TournamentTeam team) {
        this.playerTeams.put(uuid, team);
    }
    
    public TournamentTeam getPlayerTeam(final UUID uuid) {
        return this.playerTeams.get(uuid);
    }
    
    public void removePlayer(final UUID uuid) {
        this.players.remove(uuid);
    }
    
    public void addMatch(final UUID uuid) {
        this.matches.add(uuid);
    }
    
    public void removeMatch(final UUID uuid) {
        this.matches.remove(uuid);
    }
    
    public void broadcast(final String message) {
        for (final UUID uuid : this.players) {
            final Player player = this.plugin.getServer().getPlayer(uuid);
            player.sendMessage(message);
        }
    }
    
    public void broadcastWithSound(final String message, final Sound sound) {
        for (final UUID uuid : this.players) {
            final Player player = this.plugin.getServer().getPlayer(uuid);
            player.sendMessage(message);
            player.playSound(player.getLocation(), sound, 10.0f, 1.0f);
        }
    }
    
    public int decrementCountdown() {
        return --this.countdown;
    }
    
    @ConstructorProperties({ "id", "teamSize", "size", "kitName" })
    public Tournament(final int id, final int teamSize, final int size, final String kitName) {
        this.plugin = Practice.getInstance();
        this.players = new HashSet<UUID>();
        this.matches = new HashSet<UUID>();
        this.aliveTeams = new ArrayList<TournamentTeam>();
        this.playerTeams = new HashMap<UUID, TournamentTeam>();
        this.tournamentState = TournamentState.WAITING;
        this.currentRound = 1;
        this.countdown = 31;
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
    
    public void setTournamentState(final TournamentState tournamentState) {
        this.tournamentState = tournamentState;
    }
    
    public int getCurrentRound() {
        return this.currentRound;
    }
    
    public void setCurrentRound(final int currentRound) {
        this.currentRound = currentRound;
    }
    
    public int getCountdown() {
        return this.countdown;
    }
    
    public void setCountdown(final int countdown) {
        this.countdown = countdown;
    }
}
