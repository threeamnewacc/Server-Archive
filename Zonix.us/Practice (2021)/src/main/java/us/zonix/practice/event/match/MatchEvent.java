package us.zonix.practice.event.match;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import us.zonix.practice.match.Match;

public class MatchEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Match match;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Match getMatch() {
        return this.match;
    }

    public MatchEvent(Match match) {
        this.match = match;
    }
}
