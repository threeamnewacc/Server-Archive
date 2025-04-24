package me.devkevin.practice.tournament.runnable;

import club.inverted.chatcolor.CC;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.PracticeLang;
import me.devkevin.practice.kit.Kit;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.queue.QueueType;
import me.devkevin.practice.tournament.Tournament;
import me.devkevin.practice.tournament.state.TournamentState;
import me.devkevin.practice.tournament.team.TournamentTeam;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Copyright 19/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@RequiredArgsConstructor
public class TournamentRunnable extends BukkitRunnable {
    private final Practice plugin = Practice.getInstance();
    private final Tournament tournament;

    @Override
    public void run() {
        if (!this.plugin.getTournamentManager().getTournaments().isEmpty()) {
            if (this.tournament.getTournamentState() == TournamentState.STARTING) {
                int countdown = this.tournament.decrementCountdown();
                if (countdown == 0) {
                    if (this.tournament.getCurrentRound() == 1) {
                        Set<UUID> players = Sets.newConcurrentHashSet(this.tournament.getPlayers());
                        if (!tournament.isTeamTournament()) {
                            List<UUID> currentTeam = null;
                            for (UUID player : players) {
                                if (currentTeam == null) {
                                    currentTeam = new ArrayList<>();
                                }
                                currentTeam.add(player);
                                if (currentTeam.size() == this.tournament.getTeamSize()) {
                                    TournamentTeam team = new TournamentTeam(currentTeam.get(0), currentTeam);
                                    this.tournament.addAliveTeam(team);
                                    for (UUID teammate : team.getPlayers()) {
                                        tournament.setPlayerTeam(teammate, team);
                                    }
                                    currentTeam = null;
                                }
                            }
                        } else {
                            for (UUID player : players) {
                                if (this.plugin.getPartyManager().isLeader(player)) {
                                    Party party = this.plugin.getPartyManager().getParty(player);
                                    if (party != null) {
                                        TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList(party.getMembers()));
                                        this.tournament.addAliveTeam(team);
                                        for (UUID member : party.getMembers()) {
                                            if (tournament.getPlayerTeam(member) == null) {
                                                tournament.setPlayerTeam(member, team);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    List<TournamentTeam> teams = this.tournament.getAliveTeams();
                    Collections.shuffle(teams);
                    for (int i = 0; i < teams.size(); i += 2) {
                        TournamentTeam teamA = teams.get(i);
                        if (teams.size() > i + 1) {
                            TournamentTeam teamB = teams.get(i + 1);
                            for (UUID playerUUID : teamA.getAlivePlayers()) {
                                this.removeSpectator(playerUUID);
                            }
                            for (UUID playerUUID : teamB.getAlivePlayers()) {
                                this.removeSpectator(playerUUID);
                            }

                            MatchTeam matchTeamA = new MatchTeam(teamA.getLeader(), new ArrayList<>(teamA.getAlivePlayers()), 0);
                            MatchTeam matchTeamB = new MatchTeam(teamB.getLeader(), new ArrayList<>(teamB.getAlivePlayers()), 1);


                            Kit kit = this.plugin.getKitManager().getKit(this.tournament.getKitName());
                            Match match = new Match(this.plugin.getArenaManager().getRandomArena(kit, null), kit, QueueType.UN_RANKED, matchTeamA, matchTeamB);

                            Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
                            Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

                            match.broadcastMessage(CC.translate("&7Tournament match starting! " + LandCore.getInstance().getProfileManager().getProfile(leaderA.getUniqueId()).getGrant().getRank().getColor() + leaderA.getName() + " &7vs " + LandCore.getInstance().getProfileManager().getProfile(leaderB.getUniqueId()).getGrant().getRank().getColor() + leaderB.getName()));

                            this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                                this.plugin.getMatchManager().createMatch(match);
                                this.tournament.addMatch(match.getMatchId());
                                this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId());
                            });
                        } else {
                            for (UUID playerUUID : teamA.getAlivePlayers()) {
                                Player player = this.plugin.getServer().getPlayer(playerUUID);
                                player.sendMessage(CC.translate("&cYou have been skipped to the next round because there were no opponents for you this round."));
                            }
                        }
                    }

                    tournament.broadcast(" ");
                    tournament.broadcast(CC.translate("&e&lTournament"));
                    tournament.broadcast(CC.translate(" &a" + PracticeLang.VERTICAL_BAR + " &eKit: &7" + tournament.getKitName()));
                    tournament.broadcast(CC.translate(" &a" + PracticeLang.VERTICAL_BAR + " &eType: &7" + tournament.getTeamSize() + "v" + tournament.getTeamSize()));
                    tournament.broadcast(CC.translate(" &a" + PracticeLang.VERTICAL_BAR + " &ePlayers: &7" + tournament.getPlayers().size()));
                    tournament.broadcast(CC.translate(" &a" + PracticeLang.VERTICAL_BAR + " &eStarting Round: &7" + tournament.getCurrentRound()));


                    this.tournament.setTournamentState(TournamentState.FIGHTING);
                } else if ((countdown % 5 == 0 || countdown < 5) && countdown > 0) {
                    this.tournament.broadcastWithSound(CC.translate("&8[&eTournament&8] &6Round &e#" + this.tournament.getCurrentRound() +" &7is starting in &e" + countdown + "&7."), Sound.NOTE_PLING);
                }
            }
        } else {
            this.cancel();
        }
    }

    private void removeSpectator(UUID uuid) {
        Player player = this.plugin.getServer().getPlayer(uuid);
        if (player != null) {
            Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());
            if (profile.isSpectating()) {
                this.plugin.getMatchManager().removeSpectator(player);
                if (!profile.getCachedPlayer().isEmpty()) {
                    profile.getCachedPlayer().clear();
                }
            }
        }
    }
}
