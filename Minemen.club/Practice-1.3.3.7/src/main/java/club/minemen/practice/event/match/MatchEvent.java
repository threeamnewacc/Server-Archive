package club.minemen.practice.event.match;

import club.minemen.practice.match.Match;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class MatchEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();

	private final Match match;

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
}
