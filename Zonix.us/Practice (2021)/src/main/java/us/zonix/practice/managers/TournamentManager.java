package us.zonix.practice.managers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import me.maiko.dexter.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.runnable.TournamentRunnable;
import us.zonix.practice.tournament.Tournament;
import us.zonix.practice.tournament.TournamentState;
import us.zonix.practice.tournament.TournamentTeam;
import us.zonix.practice.util.TeamUtil;

public class TournamentManager {
    private final Practice plugin = Practice.getInstance();
    private final Map<UUID, Integer> players = new HashMap<>();
    private final Map<UUID, Integer> matches = new HashMap<>();
    private final Map<Integer, Tournament> tournaments = new HashMap<>();
    private final Map<Tournament, BukkitRunnable> runnables = new HashMap<>();

    public boolean isInTournament(UUID uuid) {
        return this.players.containsKey(uuid);
    }

    public Tournament getTournament(UUID uuid) {
        Integer id = this.players.get(uuid);
        return id == null ? null : this.tournaments.get(id);
    }

    public Tournament getTournamentFromMatch(UUID uuid) {
        Integer id = this.matches.get(uuid);
        return id == null ? null : this.tournaments.get(id);
    }

    public void createTournament(CommandSender commandSender, int id, int teamSize, int size, String kitName) {
        Tournament tournament = new Tournament(id, teamSize, size, kitName);
        this.tournaments.put(id, tournament);
        BukkitRunnable bukkitRunnable = new TournamentRunnable(tournament);
        bukkitRunnable.runTaskTimerAsynchronously(this.plugin, 20L, 20L);
        this.runnables.put(tournament, bukkitRunnable);
        commandSender.sendMessage(ChatColor.WHITE + "Successfully created tournament.");
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            player.performCommand("tournament alert " + id);
        }
    }

    private void playerLeft(Tournament tournament, Player player) {
        TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());
        tournament.removePlayer(player.getUniqueId());
        player.sendMessage(ChatColor.RED.toString() + "[Tournament] " + ChatColor.GRAY + "You left the tournament.");
        this.players.remove(player.getUniqueId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(
            ChatColor.RED.toString()
                + "[Tournament] "
                + Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().getGameColor()
                + ""
                + player.getName()
                + ChatColor.WHITE
                + " left the tournament. ["
                + tournament.getPlayers().size()
                + "/"
                + tournament.getSize()
                + "]"
        );
        if (team != null) {
            team.killPlayer(player.getUniqueId());
            if (team.getAlivePlayers().size() == 0) {
                tournament.killTeam(team);
                if (tournament.getAliveTeams().size() == 1) {
                    TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);
                    String names = TeamUtil.getNames(tournamentTeam);
                    String announce = ChatColor.DARK_RED
                        + names
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + tournament.getKitName()
                        + ChatColor.WHITE
                        + " tournament!";
                    Bukkit.broadcastMessage(announce);

                    for (UUID playerUUID : tournamentTeam.getAlivePlayers()) {
                        this.players.remove(playerUUID);
                        Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                        this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                    }

                    this.plugin.getTournamentManager().removeTournament(tournament.getId(), false);
                }
            } else if (team.getLeader().equals(player.getUniqueId())) {
                team.setLeader(team.getAlivePlayers().get(0));
            }
        }
    }

    private void teamEliminated(Tournament tournament, TournamentTeam winnerTeam, TournamentTeam losingTeam) {
        for (UUID playerUUID : losingTeam.getAlivePlayers()) {
            Player player = this.plugin.getServer().getPlayer(playerUUID);
            tournament.removePlayer(player.getUniqueId());
            player.sendMessage(ChatColor.RED.toString() + "[Tournament] " + ChatColor.GRAY + "You have been eliminated. " + ChatColor.GRAY);
            this.players.remove(player.getUniqueId());
        }

        String word = losingTeam.getAlivePlayers().size() > 1 ? "have" : "has";
        boolean isParty = tournament.getTeamSize() > 1;
        String announce = ChatColor.RED
            + "[Tournament] "
            + ChatColor.RED
            + (isParty ? losingTeam.getLeaderName() + "'s Party" : losingTeam.getLeaderName())
            + ChatColor.GRAY
            + " "
            + word
            + " been eliminated by "
            + ChatColor.WHITE
            + (isParty ? winnerTeam.getLeaderName() + "'s Party" : winnerTeam.getLeaderName())
            + ".";
        String alive = ChatColor.RED + "[Tournament] " + ChatColor.GRAY + "Players: (" + tournament.getPlayers().size() + "/" + tournament.getSize() + ")";
        tournament.broadcast(announce);
        tournament.broadcast(alive);
    }

    public void leaveTournament(Player player) {
        Tournament tournament = this.getTournament(player.getUniqueId());
        if (tournament != null) {
            Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
            if (party == null || tournament.getTournamentState() == TournamentState.FIGHTING) {
                this.playerLeft(tournament, player);
            } else if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                for (UUID memberUUID : party.getMembers()) {
                    Player member = this.plugin.getServer().getPlayer(memberUUID);
                    this.playerLeft(tournament, member);
                }
            } else {
                player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
            }
        }
    }

    private void playerJoined(Tournament tournament, Player player, boolean party) {
        if (Practice.getInstance().isRegionLock() && !party) {
            Profile profile = Profile.getByUuid(player.getUniqueId());
            if (profile != null && profile.hasVpnData()) {
                if (Practice.getInstance().getAllowedRegions().stream().noneMatch(profile.getVpnData().getContinentCode()::equalsIgnoreCase)) {
                    player.sendMessage(
                        ChatColor.RED
                            + "Your region does not allow you to join premium/ranked queues on this practice sub-server. Make sure you are on the right proxy and sub-server."
                    );
                    return;
                }
            } else {
                Bukkit.getOnlinePlayers()
                    .parallelStream()
                    .filter(online -> online.hasPermission("core.superadmin"))
                    .forEach(
                        online -> online.sendMessage(
                                ChatColor.RED
                                    + "[!] Couldn't find "
                                    + player.getName()
                                    + "'s region! Make sure the AntiVPN is working correctly, if not please use /regionlock toggle off to disable region-lock."
                            )
                    );
            }
        }

        tournament.addPlayer(player.getUniqueId());
        this.players.put(player.getUniqueId(), tournament.getId());
        this.plugin.getPlayerManager().sendToSpawnAndReset(player);
        tournament.broadcast(
            ChatColor.RED.toString()
                + "[Tournament] "
                + Profile.getByUuidIfAvailable(player.getUniqueId()).getRank().getGameColor()
                + ""
                + player.getName()
                + ChatColor.WHITE
                + " joined the tournament. ["
                + tournament.getPlayers().size()
                + "/"
                + tournament.getSize()
                + "]"
        );
    }

    public void joinTournament(Integer id, Player player) {
        Tournament tournament = this.tournaments.get(id);
        Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
        if (party != null) {
            if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
                if (party.getMembers().size() + tournament.getPlayers().size() <= tournament.getSize()) {
                    if (party.getMembers().size() == tournament.getTeamSize() && party.getMembers().size() != 1) {
                        for (UUID memberUUID : party.getMembers()) {
                            Player member = this.plugin.getServer().getPlayer(memberUUID);
                            this.playerJoined(tournament, member, true);
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "The party size must be of " + tournament.getTeamSize() + " players.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Sorry! The tournament is already full.");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You are not the leader of the party.");
            }
        } else {
            this.playerJoined(tournament, player, false);
        }

        if (tournament.getPlayers().size() == tournament.getSize()) {
            tournament.setTournamentState(TournamentState.STARTING);
        }
    }

    public Tournament getTournament(Integer id) {
        return this.tournaments.get(id);
    }

    public void removeTournament(Integer id, boolean force) {
        Tournament tournament = this.tournaments.get(id);
        if (tournament != null) {
            if (force) {
                for (Iterator<UUID> players = this.players.keySet().iterator(); players.hasNext(); players.remove()) {
                    UUID uuid = players.next();
                    Player player = Bukkit.getPlayer(uuid);
                    if (player != null) {
                        player.sendMessage(ChatColor.RED + "The tournament has force ended.");
                        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
                            if (tournament.getTournamentState() == TournamentState.FIGHTING) {
                                this.plugin.getMatchManager().removeFighter(player, this.plugin.getPlayerManager().getPlayerData(player.getUniqueId()), false);
                            }

                            this.plugin.getPlayerManager().sendToSpawnAndReset(player);
                        }, 2L);
                    }
                }
            }

            if (this.runnables.containsKey(tournament)) {
                this.runnables.get(tournament).cancel();
            }

            this.tournaments.remove(id);
        }
    }

    public void addTournamentMatch(UUID matchId, Integer tournamentId) {
        this.matches.put(matchId, tournamentId);
    }

    public void removeTournamentMatch(Match match) {
        Tournament tournament = this.getTournamentFromMatch(match.getMatchId());
        if (tournament != null) {
            tournament.removeMatch(match.getMatchId());
            this.matches.remove(match.getMatchId());
            MatchTeam losingTeam = match.getWinningTeamId() == 0 ? match.getTeams().get(1) : match.getTeams().get(0);
            TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getPlayers().get(0));
            tournament.killTeam(losingTournamentTeam);
            MatchTeam winningTeam = match.getTeams().get(match.getWinningTeamId());
            TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAlivePlayers().get(0));
            this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);
            if (tournament.getMatches().size() == 0) {
                if (tournament.getAliveTeams().size() > 1) {
                    tournament.setTournamentState(TournamentState.STARTING);
                    tournament.setCurrentRound(tournament.getCurrentRound() + 1);
                    tournament.setCountdown(16);
                } else {
                    String names = TeamUtil.getNames(winningTournamentTeam);
                    String announce = ChatColor.DARK_RED
                        + names
                        + ChatColor.WHITE
                        + " has won our "
                        + ChatColor.DARK_RED
                        + tournament.getKitName()
                        + ChatColor.WHITE
                        + " tournament!";
                    Bukkit.broadcastMessage(announce);

                    for (UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
                        this.players.remove(playerUUID);
                        Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
                        this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
                    }

                    this.plugin.getTournamentManager().removeTournament(tournament.getId(), false);
                }
            }
        }
    }

    public Map<Integer, Tournament> getTournaments() {
        return this.tournaments;
    }
}
