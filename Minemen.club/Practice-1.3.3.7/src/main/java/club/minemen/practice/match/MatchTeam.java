package club.minemen.practice.match;

import club.minemen.practice.team.KillableTeam;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MatchTeam extends KillableTeam {

	private final List<Integer> playerIds;
	private final int teamID;

	public MatchTeam(UUID leader, List<UUID> players, List<Integer> playerIds, int teamID) {
		super(leader, players);
		this.playerIds = playerIds;
		this.teamID = teamID;
	}
}
