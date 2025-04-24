package club.minemen.practice.managers;

import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.match.Match;
import club.minemen.practice.match.MatchTeam;
import club.minemen.practice.party.Party;
import club.minemen.practice.runnable.TournamentRunnable;
import club.minemen.practice.tournament.Tournament;
import club.minemen.practice.tournament.TournamentState;
import club.minemen.practice.tournament.TournamentTeam;
import club.minemen.practice.util.TeamUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TournamentManager {

	private final Practice plugin = Practice.getInstance();

	private final Map<UUID, Integer> players = new HashMap<>();
	private final Map<UUID, Integer> matches = new HashMap<>();
	private final Map<Integer, Tournament> tournaments = new HashMap<>();

	public boolean isInTournament(UUID uuid) {
		return this.players.containsKey(uuid);
	}

	public Tournament getTournament(UUID uuid) {
		Integer id = this.players.get(uuid);

		if (id == null) {
			return null;
		}

		return this.tournaments.get(id);
	}

	public Tournament getTournamentFromMatch(UUID uuid) {
		Integer id = this.matches.get(uuid);

		if (id == null) {
			return null;
		}

		return this.tournaments.get(id);
	}

	public void createTournament(CommandSender commandSender, int id, int teamSize, int size, String kitName) {
		Tournament tournament = new Tournament(id, teamSize, size, kitName);

		this.tournaments.put(id, tournament);

		new TournamentRunnable(tournament).runTaskTimer(this.plugin, 20L, 20L);

		commandSender.sendMessage(CC.PRIMARY + "Successfully created tournament id " + CC.SECONDARY + id + CC.PRIMARY
				+ " with team size " + CC.SECONDARY + teamSize + CC.PRIMARY + ", kit " + CC.SECONDARY + kitName + CC.PRIMARY
				+ ", and tournament size " + CC.SECONDARY + size + CC.PRIMARY + ".");
	}

	private void playerLeft(Tournament tournament, Player player) {
		TournamentTeam team = tournament.getPlayerTeam(player.getUniqueId());

		tournament.removePlayer(player.getUniqueId());

		player.sendMessage(CC.PRIMARY + "You left the tournament.");

		this.players.remove(player.getUniqueId());

		this.plugin.getPlayerManager().sendToSpawnAndReset(player);

		tournament.broadcast(CC.SECONDARY + player.getName() + CC.PRIMARY + " has left the tournament. ("
				+ CC.SECONDARY + tournament.getPlayers().size() + CC.PRIMARY + "/" + CC.SECONDARY + tournament.getSize() + CC.PRIMARY + ")");

		if (team != null) {
			team.killPlayer(player.getUniqueId());

			if (team.getAlivePlayers().size() == 0) {
				tournament.killTeam(team);

				if (tournament.getAliveTeams().size() == 1) {
					TournamentTeam tournamentTeam = tournament.getAliveTeams().get(0);

					String names = TeamUtil.getNames(tournamentTeam);

					this.plugin.getServer().broadcastMessage(names + " won Tournament " + CC.SECONDARY + tournament.getId() + CC.PRIMARY + "!");

					for (UUID playerUUID : tournamentTeam.getAlivePlayers()) {
						this.players.remove(playerUUID);
						Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
						this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
					}

					this.plugin.getTournamentManager().removeTournament(tournament.getId());
				}
			} else {
				if (team.getLeader().equals(player.getUniqueId())) {
					team.setLeader(team.getAlivePlayers().get(0));
				}
			}
		}
	}

	private void teamEliminated(Tournament tournament, TournamentTeam winnerTeam, TournamentTeam losingTeam) {
		for (UUID playerUUID : losingTeam.getAlivePlayers()) {
			Player player = this.plugin.getServer().getPlayer(playerUUID);

			tournament.removePlayer(player.getUniqueId());

			player.sendMessage(CC.RED + "You have been eliminated.");
			player.sendMessage(CC.RED + "Do /tournament status " + tournament.getId() + " to see who is left in the tournament.");

			this.players.remove(player.getUniqueId());
		}

		String word = losingTeam.getAlivePlayers().size() > 1 ? "have" : "has";

		tournament.broadcast(TeamUtil.getNames(losingTeam) + CC.PRIMARY + " " + word + " been eliminated by " +
				TeamUtil.getNames(winnerTeam) + CC.PRIMARY + ". ("
				+ CC.SECONDARY + tournament.getPlayers().size() + CC.PRIMARY + "/" + CC.SECONDARY + tournament.getSize() + CC.PRIMARY + ")");
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
				player.sendMessage(CC.RED + "You are not the leader of this party!");
			}
		} else {
			this.playerLeft(tournament, player);
		}
	}

	private void playerJoined(Tournament tournament, Player player) {
		tournament.addPlayer(player.getUniqueId());

		this.players.put(player.getUniqueId(), tournament.getId());

		this.plugin.getPlayerManager().sendToSpawnAndReset(player);

		tournament.broadcast(CC.SECONDARY + player.getName() + CC.PRIMARY + " has joined the tournament. ("
				+ CC.SECONDARY + tournament.getPlayers().size() + CC.PRIMARY + "/" + CC.SECONDARY + tournament.getSize() + CC.PRIMARY + ")");
	}

	public void joinTournament(Integer id, Player player) {
		Tournament tournament = this.tournaments.get(id);

		Party party = this.plugin.getPartyManager().getParty(player.getUniqueId());
		if (party != null) {
			if (this.plugin.getPartyManager().isLeader(player.getUniqueId())) {
				if ((party.getMembers().size() + tournament.getPlayers().size()) <= tournament.getSize()) {
					if (party.getMembers().size() != tournament.getTeamSize() || party.getMembers().size() == 1) {
						player.sendMessage(CC.RED + "You are in a party that does not match this tournament's description!");
					} else {
						for (UUID memberUUID : party.getMembers()) {
							Player member = this.plugin.getServer().getPlayer(memberUUID);

							this.playerJoined(tournament, member);
						}
					}
				} else {
					player.sendMessage(CC.RED + "This tournament is full!");
				}
			} else {
				player.sendMessage(CC.RED + "You are not the leader of this party!");
			}
		} else {
			this.playerJoined(tournament, player);
		}

		if (tournament.getPlayers().size() == tournament.getSize()) {
			tournament.setTournamentState(TournamentState.STARTING);
		}
	}

	public Tournament getTournament(Integer id) {
		return this.tournaments.get(id);
	}

	public void removeTournament(Integer id) {
		Tournament tournament = this.tournaments.get(id);

		if (tournament == null) {
			return;
		}

		this.tournaments.remove(id);
	}

	public void addTournamentMatch(UUID matchId, Integer tournamentId) {
		this.matches.put(matchId, tournamentId);
	}

	public void removeTournamentMatch(Match match) {
		Tournament tournament = this.getTournamentFromMatch(match.getMatchId());

		if (tournament == null) {
			return;
		}

		tournament.removeMatch(match.getMatchId());

		this.matches.remove(match.getMatchId());

		MatchTeam losingTeam = match.getWinningTeamId() == 0 ? match.getTeams().get(1) : match.getTeams().get(0);

		TournamentTeam losingTournamentTeam = tournament.getPlayerTeam(losingTeam.getPlayers().get(0));

		tournament.killTeam(losingTournamentTeam);

		MatchTeam winningTeam = match.getTeams().get(match.getWinningTeamId());

		TournamentTeam winningTournamentTeam = tournament.getPlayerTeam(winningTeam.getAlivePlayers().get(0));

		this.teamEliminated(tournament, winningTournamentTeam, losingTournamentTeam);

		winningTournamentTeam.broadcast(CC.PRIMARY + "Tip: If you're bored, do " + CC.SECONDARY + "/tournament status " + tournament.getId() + CC.PRIMARY + " to see the " +
				"remaining matches of this round!");

		if (tournament.getMatches().size() == 0) {
			if (tournament.getAliveTeams().size() > 1) {
				tournament.setTournamentState(TournamentState.STARTING);
				tournament.setCurrentRound(tournament.getCurrentRound() + 1);
				tournament.setCountdown(16);
			} else {
				String names = TeamUtil.getNames(winningTournamentTeam);

				this.plugin.getServer().broadcastMessage(names + " won Tournament " + CC.SECONDARY + tournament.getId() + CC.PRIMARY + "!");

				for (UUID playerUUID : winningTournamentTeam.getAlivePlayers()) {
					this.players.remove(playerUUID);
					Player tournamentPlayer = this.plugin.getServer().getPlayer(playerUUID);
					this.plugin.getPlayerManager().sendToSpawnAndReset(tournamentPlayer);
				}

				this.plugin.getTournamentManager().removeTournament(tournament.getId());
			}
		}
	}
}
