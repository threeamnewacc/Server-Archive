package net.badlion.arenapvp.event;

import net.badlion.arenapvp.Team;
import net.badlion.gberry.utils.RatingUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashMap;
import java.util.Map;

public class MatchStartEvent extends Event {

	private static final HandlerList handlers = new HandlerList();


	private Map<Player, Boolean> team1 = new HashMap<>();
	private Map<Player, Boolean> team2 = new HashMap<>();

	private RatingUtil.Rank team1Rank = null;

	private RatingUtil.Rank team2Rank = null;

	public MatchStartEvent(Team team1, Team team2, RatingUtil.Rank team1Rank, RatingUtil.Rank team2Rank) {
		for (Player member : team1.members()) {
			this.team1.put(member, true);
		}
		for (Player member : team2.members()) {
			this.team2.put(member, true);
		}

		try {
			this.team1Rank = team1Rank;
			this.team2Rank = team2Rank;
		} catch (NullPointerException ex) {
			// Ignore
		}
	}

	public Map<Player, Boolean> getTeam1() {
		return team1;
	}

	public Map<Player, Boolean> getTeam2() {
		return team2;
	}

	public RatingUtil.Rank getTeam1Rank() {
		return team1Rank;
	}

	public RatingUtil.Rank getTeam2Rank() {
		return team2Rank;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
