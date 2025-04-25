package us.zonix.practice.runnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.practice.Practice;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.queue.QueueType;
import us.zonix.practice.tournament.Tournament;
import us.zonix.practice.tournament.TournamentState;
import us.zonix.practice.tournament.TournamentTeam;

public class TournamentRunnable extends BukkitRunnable {
    private final Practice plugin = Practice.getInstance();
    private final Tournament tournament;

    public void run() {
        if (this.tournament.getTournamentState() == TournamentState.STARTING) {
            int countdown = this.tournament.decrementCountdown();
            if (countdown == 0) {
                if (this.tournament.getCurrentRound() == 1) {
                    Set<UUID> players = Sets.newConcurrentHashSet(this.tournament.getPlayers());

                    for (UUID player : players) {
                        Party party = this.plugin.getPartyManager().getParty(player);
                        if (party != null) {
                            TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList(party.getMembers()));
                            this.tournament.addAliveTeam(team);

                            for (UUID member : party.getMembers()) {
                                players.remove(member);
                                this.tournament.setPlayerTeam(member, team);
                            }
                        }
                    }

                    List<UUID> currentTeam = null;

                    for (UUID playerx : players) {
                        if (currentTeam == null) {
                            currentTeam = new ArrayList<>();
                        }

                        currentTeam.add(playerx);
                        if (currentTeam.size() == this.tournament.getTeamSize()) {
                            TournamentTeam team = new TournamentTeam(currentTeam.get(0), currentTeam);
                            this.tournament.addAliveTeam(team);

                            for (UUID teammate : team.getPlayers()) {
                                this.tournament.setPlayerTeam(teammate, team);
                            }

                            currentTeam = null;
                        }
                    }
                }

                List<TournamentTeam> teams = this.tournament.getAliveTeams();
                Collections.shuffle(teams);

                for (int i = 0; i < teams.size(); i += 2) {
                    TournamentTeam teamA = teams.get(i);
                    if (teams.size() <= i + 1) {
                        for (UUID playerUUID : teamA.getAlivePlayers()) {
                            Player playerx = this.plugin.getServer().getPlayer(playerUUID);
                            playerx.sendMessage(
                                ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
                            );
                            playerx.sendMessage(ChatColor.RED + "You have been skipped to the next round.");
                            playerx.sendMessage(ChatColor.RED + "There was no matching team for you.");
                            playerx.sendMessage(
                                ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------"
                            );
                        }
                    } else {
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
                        Match match = new Match(this.plugin.getArenaManager().getRandomArena(kit), kit, QueueType.UNRANKED, false, matchTeamA, matchTeamB);
                        Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
                        Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
                        match.broadcast(
                            ChatColor.RED + "Starting tournament match. " + ChatColor.WHITE + "(" + leaderA.getName() + " vs " + leaderB.getName() + ")"
                        );
                        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                            this.plugin.getMatchManager().createMatch(match);
                            this.tournament.addMatch(match.getMatchId());
                            this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), this.tournament.getId());
                        });
                    }
                }

                this.tournament.broadcast(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                this.tournament
                    .broadcast(
                        ChatColor.RED.toString()
                            + ChatColor.BOLD
                            + "TOURNAMENT ["
                            + this.tournament.getTeamSize()
                            + "v"
                            + this.tournament.getTeamSize()
                            + "] "
                            + this.tournament.getKitName()
                    );
                this.tournament
                    .broadcast(ChatColor.GOLD.toString() + ChatColor.BOLD + "* " + ChatColor.WHITE + "Starting Round #" + this.tournament.getCurrentRound());
                this.tournament.broadcast(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                this.tournament.setTournamentState(TournamentState.FIGHTING);
            } else if (countdown <= 5) {
                String announce = ChatColor.RED
                    + "[Tournament] "
                    + ChatColor.WHITE
                    + "Round #"
                    + this.tournament.getCurrentRound()
                    + " is starting in "
                    + ChatColor.RED
                    + countdown
                    + ChatColor.WHITE
                    + ".";
                this.tournament.broadcast(announce);
            }
        }
    }

    private void removeSpectator(UUID playerUUID) {
        Player player = this.plugin.getServer().getPlayer(playerUUID);
        if (player != null) {
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                this.plugin.getMatchManager().removeSpectator(player);
            }
        }
    }

    public TournamentRunnable(Tournament tournament) {
        this.tournament = tournament;
    }
}
