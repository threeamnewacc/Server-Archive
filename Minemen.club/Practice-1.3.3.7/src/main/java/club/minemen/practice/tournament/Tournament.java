package club.minemen.practice.tournament;

import club.minemen.practice.Practice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class Tournament {
	private final Practice plugin = Practice.getInstance();

	@Getter
	private final Set<UUID> players = new HashSet<>();
	@Getter
	private final Set<UUID> matches = new HashSet<>();
	@Getter
	private final List<TournamentTeam> aliveTeams = new ArrayList<>();
	@Getter
	private final Map<UUID, TournamentTeam> playerTeams = new HashMap<>();
	@Getter
	private final int id;
	@Getter
	private final int teamSize;
	@Getter
	private final int size;
	@Getter
	private final String kitName;
	@Getter
	@Setter
	private TournamentState tournamentState = TournamentState.WAITING;
	@Getter
	@Setter
	private int currentRound = 1;
	@Getter
	@Setter
	private int countdown = 31;

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
			player.playSound(player.getLocation(), sound, 10, 1);
		}
	}

	public int decrementCountdown() {
		return --this.countdown;
	}
}
