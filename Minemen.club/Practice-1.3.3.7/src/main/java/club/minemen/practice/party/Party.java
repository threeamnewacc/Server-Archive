package club.minemen.practice.party;

import club.minemen.practice.Practice;
import club.minemen.practice.match.MatchTeam;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class Party {

	private final Practice plugin = Practice.getInstance();

	private final UUID leader;
	private final Set<UUID> members = new HashSet<>();
	private int limit = 50;
	private boolean open;

	public Party(UUID leader) {
		this.leader = leader;
		this.members.add(leader);
	}

	public void addMember(UUID uuid) {
		this.members.add(uuid);
	}

	public void removeMember(UUID uuid) {
		this.members.remove(uuid);
	}

	public void broadcast(String message) {
		this.members().forEach(member -> member.sendMessage(message));
	}

	public MatchTeam[] split() {
		List<UUID> teamA = new ArrayList<>();
		List<UUID> teamB = new ArrayList<>();

		for (UUID member : this.members) {
			if (teamA.size() == teamB.size()) {
				teamA.add(member);
			} else {
				teamB.add(member);
			}
		}

		return new MatchTeam[]{
				new MatchTeam(teamA.get(0), teamA, null, 0),
				new MatchTeam(teamB.get(0), teamB, null, 1)
		};
	}

	public Stream<Player> members() {
		return this.members.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull);
	}

}
