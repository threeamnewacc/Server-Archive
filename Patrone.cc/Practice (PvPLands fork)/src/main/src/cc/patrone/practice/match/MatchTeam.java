package cc.patrone.practice.match;

import cc.patrone.practice.player.PlayerState;
import cc.patrone.practice.player.PracticeProfile;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class MatchTeam {
	private final int teamId;
	private final PracticeProfile leader;
	@Getter
	private final List<PracticeProfile> players;
	@Setter
	private Match match;

	public List<PracticeProfile> aliveProfiles() {
		return players.stream().filter(player -> player.getPlayerState() == PlayerState.FIGHTING && match != null
				&& match.getMatchData(player).isPlayerInMatch()).collect(Collectors.toList());
	}

	public List<Player> alivePlayers() {
		return aliveProfiles().stream().map(PracticeProfile::asPlayer).collect(Collectors.toList());
	}

	int remainingPlayers() {
		return aliveProfiles().size();
	}
}
