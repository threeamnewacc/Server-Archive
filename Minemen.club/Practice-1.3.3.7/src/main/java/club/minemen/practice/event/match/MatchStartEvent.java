package club.minemen.practice.event.match;

import club.minemen.practice.match.Match;

public class MatchStartEvent extends MatchEvent {
	public MatchStartEvent(Match match) {
		super(match);
	}
}
