package me.devkevin.practice.tournament.manager;

import club.inverted.chatcolor.CC;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.match.Match;
import me.devkevin.practice.match.MatchTeam;
import me.devkevin.practice.party.Party;
import me.devkevin.practice.tournament.Tournament;
import me.devkevin.practice.tournament.runnable.TournamentRunnable;
import me.devkevin.practice.tournament.state.TournamentState;
import me.devkevin.practice.tournament.team.TournamentTeam;
import me.devkevin.practice.util.TeamUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Copyright 19/03/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TournamentManager {
    private final Practice plugin = Practice.getInstance();

    private final Map<UUID, Integer> tournamentPlayers = new HashMap<>();
    private final Map<UUID, Integer> tournamentMatches = new HashMap<>();
    private final Map<Integer, Tournament> tournaments = new HashMap<>();

    public boolean isInTournament(UUID uuid) {
        return this.tournamentPlayers.containsKey(uuid);
    }

    public Tournament getTournament(UUID uuid) {
        Integer id = this.tournamentPlayers.get(uuid);
        if (id == null) {
            return null;
        }
        return this.tournaments.get(id);
    }

    public Tournament getTournamentFromMatch(UUID uuid) {
        Integer id = this.tournamentMatches.get(uuid);
        if (id == null) {
            return null;
        }

        return this.tournaments.get(id);
    }

    public void createTournament(CommandSender sender, int teamSize, int size, String kitName) {
        Tournament tournament = new Tournament(0, teamSize, size, kitName);
        this.tournaments.put(0, tournament);
        new TournamentRunnable(tournament).runTaskTimerAsynchronously(this.plugin, 20L, 20L);

        sender.sendMessage(CC.translate("&7Successfully created &e" + teamSize + "v" + teamSize + " &6" + kitName + " &7tournament. (Max players: &e" + size + "&7)"));

        if (sender instanceof Player) {
            Player player = (Player)sender;
            tournament.setHost(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (tournament.getCountdown() >= 5 && tournament.getTournamentState() == TournamentState.WAITING) {
                        player.performCommand("tournament start");
                    }
                }
            }.runTaskTimer(this.plugin, 0L, 30L);
        }
    }

    private void playerLeft(Tournament tournament, Player player) {
        TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
        tournament.removePlayer(player.getUniqueId());
        player.sendMessage(CC.translate("&cYou left the tournament."));

        this.tournamentPlayers.remove(player.getUniqueId());
        this.plugin.getProfileManager().sendToSpawn(player);

        tournament.broadcast(CC.translate("&8[&eTournament&8] " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + "" + " &cleft the tournament. &8(&7"+ tournament.getPlayers().size() + "/" + tournament.getSize() + "&8)"));

        if (team != null) {
            team.killPlayer(player.getUniqueId());
            if (team.getAlivePlayers().size() == 0) {
                tournament.killTeam(team);
                if (tournament.getAliveTeams().size() == 1) {
                    TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);
                    String names = TeamUtil.getNames(tournamentTeam);

                    Bukkit.broadcastMessage(CC.translate("&6" + names + " &7won the &7" + tournament.getTeamSize() + "v" + tournament.getTeamSize() +" &e" + tournament.getKitName() + " &7Tournament!"));

                    for (UUID uuid : tournamentTeam.getAlivePlayers()) {
                        this.tournamentPlayers.remove(uuid);
                        Player tournamentPlayer = this.plugin.getServer().getPlayer(uuid);
                        this.plugin.getProfileManager().sendToSpawn(tournamentPlayer);
                    }

                    removeTournament();
                }
            } else if (team.getLeader().equals(player.getUniqueId())) {
                team.setLeader(team.getAlivePlayers().get(0));
            }
        }
    }

    private void teamEliminated(Tournament tournament, TournamentTeam winnerTeam, TournamentTeam losingTeam) {
        for (UUID playerUUID : losingTeam.getPlayers()) {
            Player player = this.plugin.getServer().getPlayer(playerUUID);
            if (player != null) {
                tournament.removePlayer(player.getUniqueId());
                player.sendMessage(CC.translate("&8[&eTournament&8] &cYou have been eliminated from the Tournament. You can see the status with /tournament status"));

                this.tournamentPlayers.remove(player.getUniqueId());
            }
        }

        if (!tournament.isTeamTournament()) {
            tournament.broadcast(CC.translate("&8[&eTournament&8] " + LandCore.getInstance().getProfileManager().getProfile(losingTeam.getLeader()).getGrant().getRank().getColor() + losingTeam.getLeaderName() + " &7has been eliminated by " + LandCore.getInstance().getProfileManager().getProfile(winnerTeam.getLeader()).getGrant().getRank().getColor() + winnerTeam.getLeaderName() + "&7."));
        } else {
            tournament.broadcast(CC.translate("&8[&eTournament&8] " + LandCore.getInstance().getProfileManager().getProfile(losingTeam.getLeader()).getGrant().getRank().getColor() + losingTeam.getLeaderName() + "&7's party &7has been eliminated by " + LandCore.getInstance().getProfileManager().getProfile(winnerTeam.getLeader()).getGrant().getRank().getColor() + winnerTeam.getLeaderName() + "&7's party."));
        }
        tournament.broadcast(CC.translate("&8[&eTournament&8] &e" + tournament.getPlayers().size() + " &7remaining out of &e" + tournament.getSize()));
    }

    public void leaveTournament(Player player) {
        Tournament tournament = this.getTournament(player.getUniqueId());
        if (tournament == null) {
            return;
        }

        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null && tournament.getTournamentState() != TournamentState.FIGHTING) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                for (UUID memberUUID : party.getMembers()) {
                    Player member = this.plugin.getServer().getPlayer(memberUUID);
                    this.playerLeft(tournament, member);
                }
            } else {
                player.sendMessage(CC.translate("&cYou are not the leader of the party."));
            }
        } else {
            this.playerLeft(tournament, player);
        }
    }

    private void playerJoined(Tournament tournament, Player player) {
        tournament.addPlayer(player.getUniqueId());

        this.tournamentPlayers.put(player.getUniqueId(), tournament.getId());
        this.plugin.getProfileManager().sendToSpawn(player);

        tournament.broadcast(CC.translate("&8[&eTournament&8] " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + " &ajoined the tournament. &8(&7"+ tournament.getPlayers().size() + "/" + tournament.getSize() + "&8)"));
    }

    public void joinTournament(Integer id, Player player) {
        Tournament tournament = this.tournaments.get(id);
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                if ((party.getMembers().size() + tournament.getPlayers().size()) <= tournament.getSize()) {
                    if (party.getMembers().size() != tournament.getTeamSize() || party.getMembers().size() == 1) {
                        player.sendMessage(CC.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
                    } else {
                        for (UUID memberUUID : party.getMembers()) {
                            Player member = this.plugin.getServer().getPlayer(memberUUID);
                            this.playerJoined(tournament, member);
                        }
                    }
                } else {
                    player.sendMessage(CC.RED + "Sorry! The tournament is already full.");
                }
            } else {
                player.sendMessage(CC.translate("&cYou are not the leader of the party."));
            }
        } else {
            this.playerJoined(tournament, player);
        }

        if (tournament.getPlayers().size() == tournament.getSize()) {
            tournament.setTournamentState(TournamentState.STARTING);
        }
    }

    public Tournament getTournament() {
        return this.tournaments.get(0);
    }

    public void removeTournament() {
        Tournament tournament = this.tournaments.get(0);
        if (tournament == null) {
            return;
        }

        this.tournaments.remove(0);
    }

    public void addTournamentMatch(UUID matchId) {
        this.tournamentMatches.put(matchId, 0);
    }

    public void removeTournamentMatch(Match match) {
        Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
        if (tournament == null) {
            return;
        }

        tournament.removeMatch(match.getMatchId());
        this.tournamentMatches.remove(match.getMatchId());

        MatchTeam losingTeam = match.getWinningTeamId() == 0 ? match.getTeams().get(1) : match.getTeams().get(0);
        TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getLeader());

        MatchTeam winningTeam = match.getTeams().get(match.getWinningTeamId());
        TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getLeader());

        if (losingTournamentTeam != null) {
            tournament.killTeam(losingTournamentTeam);
            this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);
        }

        if (tournament.getMatches().size() == 0) {
            if (tournament.getAliveTeams().size() > 1) {
                tournament.setTournamentState(TournamentState.STARTING);
                tournament.setCurrentRound(tournament.getCurrentRound() + 1);
                tournament.setCountdown(11);
            } else {
                String names = TeamUtil.getNames(winningTournamentTeam);
                Bukkit.broadcastMessage(CC.translate("&6" + names + " &7won the &7" + tournament.getTeamSize() + "v" + tournament.getTeamSize() +" &e" + tournament.getKitName() + " &7Tournament!"));

                for (UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
                    this.tournamentPlayers.remove(playerUUID);
                    Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                    this.plugin.getProfileManager().sendToSpawn(tournamentPlayer);
                }

                removeTournament();
            }
        }
    }

    public Map<Integer, Tournament> getTournaments() {
        return tournaments;
    }
}
