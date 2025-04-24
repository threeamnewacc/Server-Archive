package club.minemen.practice.team;

import club.minemen.practice.Practice;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
public class KillableTeam {

	protected final Practice plugin = Practice.getInstance();

	private final List<UUID> players;
	private final List<UUID> alivePlayers = new ArrayList<>();
	private final String leaderName;
	@Setter
	private UUID leader;

	public KillableTeam(UUID leader, List<UUID> players) {
		this.leader = leader;
		this.leaderName = this.plugin.getServer().getPlayer(leader).getName();
		this.players = players;
		this.alivePlayers.addAll(players);
	}

	public void killPlayer(UUID playerUUID) {
		this.alivePlayers.remove(playerUUID);
	}

	public Stream<Player> alivePlayers() {
		return this.alivePlayers.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
	}

	public Stream<Player> players() {
		return this.players.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
	}
}
