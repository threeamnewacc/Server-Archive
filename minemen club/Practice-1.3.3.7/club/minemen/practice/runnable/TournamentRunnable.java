// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.runnable;

import java.beans.ConstructorProperties;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Player;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.party.Party;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Sound;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.match.Match;
import club.minemen.practice.queue.QueueType;
import club.minemen.practice.match.MatchTeam;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import club.minemen.practice.tournament.TournamentTeam;
import com.google.common.collect.Lists;
import java.util.UUID;
import com.google.common.collect.Sets;
import club.minemen.practice.tournament.TournamentState;
import club.minemen.practice.tournament.Tournament;
import club.minemen.practice.Practice;
import org.bukkit.scheduler.BukkitRunnable;

public class TournamentRunnable extends BukkitRunnable
{
    private final Practice plugin;
    private final Tournament tournament;
    
    public void run() {
        if (this.tournament.getTournamentState() == TournamentState.STARTING) {
            final int countdown = this.tournament.decrementCountdown();
            if (countdown == 0) {
                if (this.tournament.getCurrentRound() == 1) {
                    final Set<UUID> players = Sets.newConcurrentHashSet((Iterable)this.tournament.getPlayers());
                    for (final UUID player : players) {
                        final Party party = this.plugin.getPartyManager().getParty(player);
                        if (party != null) {
                            final TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList((Iterable)party.getMembers()));
                            this.tournament.addAliveTeam(team);
                            for (final UUID member : party.getMembers()) {
                                players.remove(member);
                                this.tournament.setPlayerTeam(member, team);
                            }
                        }
                    }
                    List<UUID> currentTeam = null;
                    for (final UUID player2 : players) {
                        if (currentTeam == null) {
                            currentTeam = new ArrayList<UUID>();
                        }
                        currentTeam.add(player2);
                        if (currentTeam.size() == this.tournament.getTeamSize()) {
                            final TournamentTeam team = new TournamentTeam(currentTeam.get(0), currentTeam);
                            this.tournament.addAliveTeam(team);
                            for (final UUID teammate : team.getPlayers()) {
                                this.tournament.setPlayerTeam(teammate, team);
                            }
                            currentTeam = null;
                        }
                    }
                }
                final List<TournamentTeam> teams = this.tournament.getAliveTeams();
                Collections.shuffle(teams);
                for (int i = 0; i < teams.size(); i += 2) {
                    final TournamentTeam teamA = teams.get(i);
                    if (teams.size() > i + 1) {
                        final TournamentTeam teamB = teams.get(i + 1);
                        for (final UUID playerUUID : teamA.getAlivePlayers()) {
                            this.removeSpectator(playerUUID);
                        }
                        for (final UUID playerUUID : teamB.getAlivePlayers()) {
                            this.removeSpectator(playerUUID);
                        }
                        final MatchTeam matchTeamA = new MatchTeam(teamA.getLeader(), new ArrayList<UUID>(teamA.getAlivePlayers()), null, 0);
                        final MatchTeam matchTeamB = new MatchTeam(teamB.getLeader(), new ArrayList<UUID>(teamB.getAlivePlayers()), null, 1);
                        final Kit kit = this.plugin.getKitManager().getKit(this.tournament.getKitName());
                        final Match match = new Match(this.plugin.getArenaManager().getRandomArena(kit), kit, QueueType.UNRANKED, new MatchTeam[] { matchTeamA, matchTeamB });
                        final Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
                        final Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
                        match.broadcast(CC.PRIMARY + "Starting a match with kit " + CC.SECONDARY + kit.getName() + CC.PRIMARY + " between " + CC.SECONDARY + leaderA.getName() + CC.PRIMARY + " and " + CC.SECONDARY + leaderB.getName() + CC.PRIMARY + ".");
                        this.plugin.getMatchManager().createMatch(match);
                        this.tournament.addMatch(match.getMatchId());
                        this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), this.tournament.getId());
                    }
                    else {
                        for (final UUID playerUUID2 : teamA.getAlivePlayers()) {
                            final Player player3 = this.plugin.getServer().getPlayer(playerUUID2);
                            player3.sendMessage(CC.PRIMARY + "You will be byed this round.");
                        }
                    }
                }
                final StringBuilder builder = new StringBuilder();
                builder.append(CC.SECONDARY).append("Round ").append(this.tournament.getCurrentRound()).append(CC.PRIMARY).append(" has started!\n");
                builder.append(CC.PRIMARY).append("Tip: Use ").append(CC.SECONDARY).append("/tournament status ").append(this.tournament.getId()).append(CC.PRIMARY).append(" to see who's fighting + the status of the tournament!");
                this.tournament.broadcastWithSound(builder.toString(), Sound.FIREWORK_BLAST);
                this.tournament.setTournamentState(TournamentState.FIGHTING);
            }
            else if ((countdown % 5 == 0 || countdown < 5) && countdown > 0) {
                this.tournament.broadcastWithSound(CC.SECONDARY + "Round " + this.tournament.getCurrentRound() + CC.PRIMARY + " is starting in " + CC.SECONDARY + countdown + CC.PRIMARY + " seconds!", Sound.CLICK);
            }
        }
    }
    
    private void removeSpectator(final UUID playerUUID) {
        final Player player = this.plugin.getServer().getPlayer(playerUUID);
        if (player != null) {
            final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                this.plugin.getMatchManager().removeSpectator(player);
            }
        }
    }
    
    @ConstructorProperties({ "tournament" })
    public TournamentRunnable(final Tournament tournament) {
        this.plugin = Practice.getInstance();
        this.tournament = tournament;
    }
}
