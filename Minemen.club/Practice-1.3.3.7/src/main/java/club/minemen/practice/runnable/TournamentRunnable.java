package club.minemen.practice.runnable;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.party.Party;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.player.PlayerState;
import club.minemen.practice.queue.QueueType;
import club.minemen.practice.tournament.Tournament;
import club.minemen.practice.tournament.TournamentState;
import club.minemen.practice.tournament.TournamentTeam;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class TournamentRunnable extends BukkitRunnable {

	private final Practice plugin = Practice.getInstance();
	private final Tournament tournament;

	@Override
	public void run() {
		if (this.tournament.getTournamentState() == TournamentState.STARTING) {
			int countdown = this.tournament.decrementCountdown();
			if (countdown == 0) {
				if (this.tournament.getCurrentRound() == 1) {
					Set<UUID> players = Sets.newConcurrentHashSet(this.tournament.getPlayers());

					//Making Teams
					for (UUID player : players) {
						Party party = this.plugin.getPartyManager().getParty(player);

						if (party != null) {
							TournamentTeam team = new TournamentTeam(party.getLeader(), Lists.newArrayList(party.getMembers()));
							this.tournament.addAliveTeam(team);
							for (UUID member : party.getMembers()) {
								players.remove(member);
								tournament.setPlayerTeam(member, team);
							}
						}
					}

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

						MatchTeam matchTeamA = new MatchTeam(teamA.getLeader(), new ArrayList<>(teamA.getAlivePlayers()), null, 0);
						MatchTeam matchTeamB = new MatchTeam(teamB.getLeader(), new ArrayList<>(teamB.getAlivePlayers()), null, 1);

						Kit kit = this.plugin.getKitManager().getKit(this.tournament.getKitName());

						Match match = new Match
								(this.plugin.getArenaManager().getRandomArena(kit), kit, QueueType.UNRANKED, matchTeamA, matchTeamB);

						Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
						Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());

						match.broadcast(CC.PRIMARY + "Starting a match with kit " + CC.SECONDARY + kit.getName() +
								CC.PRIMARY + " between " + CC.SECONDARY + leaderA.getName() + CC.PRIMARY + " and " + CC.SECONDARY + leaderB.getName() + CC.PRIMARY + ".");

						this.plugin.getMatchManager().createMatch(match);

						this.tournament.addMatch(match.getMatchId());

						this.plugin.getTournamentManager().addTournamentMatch(match.getMatchId(), tournament.getId());
					} else {
						for (UUID playerUUID : teamA.getAlivePlayers()) {
							Player player = this.plugin.getServer().getPlayer(playerUUID);

							player.sendMessage(CC.PRIMARY + "You will be byed this round.");
						}
					}
				}

				StringBuilder builder = new StringBuilder();

				builder.append(CC.SECONDARY).append("Round ").append(this.tournament.getCurrentRound()).append(CC.PRIMARY).append(" has started!\n");
				builder.append(CC.PRIMARY).append("Tip: Use ").append(CC.SECONDARY).append("/tournament status ").append(this.tournament.getId()).append(CC.PRIMARY)
						.append(" to see who's fighting + the status of the tournament!");

				this.tournament.broadcastWithSound(builder.toString(), Sound.FIREWORK_BLAST);

				this.tournament.setTournamentState(TournamentState.FIGHTING);
			} else if ((countdown % 5 == 0 || countdown < 5) && countdown > 0) {
				this.tournament.broadcastWithSound(CC.SECONDARY + "Round " + this.tournament.getCurrentRound() + CC.PRIMARY
						+ " is starting in " + CC.SECONDARY + countdown + CC.PRIMARY + " seconds!", Sound.CLICK);
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
}
