// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.managers;

import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.match.Match;
import club.minemen.practice.party.Party;
import club.minemen.practice.tournament.TournamentState;
import java.util.Iterator;
import club.minemen.practice.team.KillableTeam;
import club.minemen.practice.util.TeamUtil;
import club.minemen.practice.tournament.TournamentTeam;
import org.bukkit.entity.Player;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.plugin.Plugin;
import club.minemen.practice.runnable.TournamentRunnable;
import org.bukkit.command.CommandSender;
import java.util.HashMap;
import club.minemen.practice.tournament.Tournament;
import java.util.UUID;
import java.util.Map;
import club.minemen.practice.Practice;

public class TournamentManager
{
    private final Practice plugin;
    private final Map<UUID, Integer> players;
    private final Map<UUID, Integer> matches;
    private final Map<Integer, Tournament> tournaments;
    
    public TournamentManager() {
        this.plugin = Practice.getInstance();
        this.players = new HashMap<UUID, Integer>();
        this.matches = new HashMap<UUID, Integer>();
        this.tournaments = new HashMap<Integer, Tournament>();
    }
    
    public boolean isInTournament(final UUID uuid) {
        return this.players.containsKey(uuid);
    }
    
    public Tournament getTournament(final UUID uuid) {
        final Integer id = this.players.get(uuid);
        if (id == null) {
            return null;
        }
        return this.tournaments.get(id);
    }
    
    public Tournament getTournamentFromMatch(final UUID uuid) {
        final Integer id = this.matches.get(uuid);
        if (id == null) {
            return null;
        }
        return this.tournaments.get(id);
    }
    
    public void createTournament(final CommandSender commandSender, final int id, final int teamSize, final int size, final String kitName) {
        final Tournament tournament = new Tournament(id, teamSize, size, kitName);
        this.tournaments.put(id, tournament);
        new TournamentRunnable(tournament).runTaskTimer((Plugin)this.plugin, 20L, 20L);
        commandSender.sendMessage(CC.PRIMARY + "Successfully created tournament id " + CC.SECONDARY + id + CC.PRIMARY + " with team size " + CC.SECONDARY + teamSize + CC.PRIMARY + ", kit " + CC.SECONDARY + kitName + CC.PRIMARY + ", and tournament size " + CC.SECONDARY + size + CC.PRIMARY + ".");
    }
    
    private void playerLeft(final Tournament tournament, final Player player) {
        final TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
        tournament.removePlayer(player.getUniqueId());
        player.sendMessage(CC.PRIMARY + "You left the tournament.");
        this.players.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(CC.SECONDARY + player.getName() + CC.PRIMARY + " has left the tournament. (" + CC.SECONDARY + tournament.getPlayers().size() + CC.PRIMARY + "/" + CC.SECONDARY + tournament.getSize() + CC.PRIMARY + ")");
        if (team != null) {
            team.killPlayer(player.getUniqueId());
            if (team.getAlivePlayers().size() == 0) {
                tournament.killTeam(team);
                if (tournament.getAliveTeams().size() == 1) {
                    final TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);
                    final String names = TeamUtil.getNames(tournamentTeam);
                    this.plugin.getServer().broadcastMessage(names + " won Tournament " + CC.SECONDARY + tournament.getId() + CC.PRIMARY + "!");
                    for (final UUID playerUUID : tournamentTeam.getAlivePlayers()) {
                        this.players.remove(playerUUID);
                        final Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                        this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                    }
                    this.plugin.getTournamentManager().removeTournament(tournament.getId());
                }
            }
            else if (team.getLeader().equals(player.getUniqueId())) {
                team.setLeader(team.getAlivePlayers().get(0));
            }
        }
    }
    
    private void teamEliminated(final Tournament tournament, final TournamentTeam winnerTeam, final TournamentTeam losingTeam) {
        for (final UUID playerUUID : losingTeam.getAlivePlayers()) {
            final Player player = this.plugin.getServer().getPlayer(playerUUID);
            tournament.removePlayer(player.getUniqueId());
            player.sendMessage(CC.RED + "You have been eliminated.");
            player.sendMessage(CC.RED + "Do /tournament status " + tournament.getId() + " to see who is left in the tournament.");
            this.players.remove(player.getUniqueId());
        }
        final String word = (losingTeam.getAlivePlayers().size() > 1) ? "have" : "has";
        tournament.broadcast(TeamUtil.getNames(losingTeam) + CC.PRIMARY + " " + word + " been eliminated by " + TeamUtil.getNames(winnerTeam) + CC.PRIMARY + ". (" + CC.SECONDARY + tournament.getPlayers().size() + CC.PRIMARY + "/" + CC.SECONDARY + tournament.getSize() + CC.PRIMARY + ")");
    }
    
    public void leaveTournament(final Player player) {
        final Tournament tournament = this.getTournament(player.getUniqueId());
        if (tournament == null) {
            return;
        }
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && tournament.getTournamentState() != TournamentState.FIGHTING) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                for (final UUID memberUUID : party.getMembers()) {
                    final Player member = this.plugin.getServer().getPlayer(memberUUID);
                    this.playerLeft(tournament, member);
                }
            }
            else {
                player.sendMessage(CC.RED + "You are not the leader of this party!");
            }
        }
        else {
            this.playerLeft(tournament, player);
        }
    }
    
    private void playerJoined(final Tournament tournament, final Player player) {
        tournament.addPlayer(player.getUniqueId());
        this.players.put(player.getUniqueId(), tournament.getId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(CC.SECONDARY + player.getName() + CC.PRIMARY + " has joined the tournament. (" + CC.SECONDARY + tournament.getPlayers().size() + CC.PRIMARY + "/" + CC.SECONDARY + tournament.getSize() + CC.PRIMARY + ")");
    }
    
    public void joinTournament(final Integer id, final Player player) {
        final Tournament tournament = this.tournaments.get(id);
        final Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                if (party.getMembers().size() + tournament.getPlayers().size() <= tournament.getSize()) {
                    if (party.getMembers().size() != tournament.getTeamSize() || party.getMembers().size() == 1) {
                        player.sendMessage(CC.RED + "You are in a party that does not match this tournament's description!");
                    }
                    else {
                        for (final UUID memberUUID : party.getMembers()) {
                            final Player member = this.plugin.getServer().getPlayer(memberUUID);
                            this.playerJoined(tournament, member);
                        }
                    }
                }
                else {
                    player.sendMessage(CC.RED + "This tournament is full!");
                }
            }
            else {
                player.sendMessage(CC.RED + "You are not the leader of this party!");
            }
        }
        else {
            this.playerJoined(tournament, player);
        }
        if (tournament.getPlayers().size() == tournament.getSize()) {
            tournament.setTournamentState(TournamentState.STARTING);
        }
    }
    
    public Tournament getTournament(final Integer id) {
        return this.tournaments.get(id);
    }
    
    public void removeTournament(final Integer id) {
        final Tournament tournament = this.tournaments.get(id);
        if (tournament == null) {
            return;
        }
        this.tournaments.remove(id);
    }
    
    public void addTournamentMatch(final UUID matchId, final Integer tournamentId) {
        this.matches.put(matchId, tournamentId);
    }
    
    public void removeTournamentMatch(final Match match) {
        final Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
        if (tournament == null) {
            return;
        }
        tournament.removeMatch(match.getMatchId());
        this.matches.remove(match.getMatchId());
        final MatchTeam losingTeam = (match.getWinningTeamId() == 0) ? match.getTeams().get(1) : match.getTeams().get(0);
        final TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getPlayers().get(0));
        tournament.killTeam(losingTournamentTeam);
        final MatchTeam winningTeam = match.getTeams().get(match.getWinningTeamId());
        final TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAlivePlayers().get(0));
        this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);
        winningTournamentTeam.broadcast(CC.PRIMARY + "Tip: If you're bored, do " + CC.SECONDARY + "/tournament status " + tournament.getId() + CC.PRIMARY + " to see the remaining matches of this round!");
        if (tournament.getMatches().size() == 0) {
            if (tournament.getAliveTeams().size() > 1) {
                tournament.setTournamentState(TournamentState.STARTING);
                tournament.setCurrentRound(tournament.getCurrentRound() + 1);
                tournament.setCountdown(16);
            }
            else {
                final String names = TeamUtil.getNames(winningTournamentTeam);
                this.plugin.getServer().broadcastMessage(names + " won Tournament " + CC.SECONDARY + tournament.getId() + CC.PRIMARY + "!");
                for (final UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
                    this.players.remove(playerUUID);
                    final Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                    this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                }
                this.plugin.getTournamentManager().removeTournament(tournament.getId());
            }
        }
    }
}
