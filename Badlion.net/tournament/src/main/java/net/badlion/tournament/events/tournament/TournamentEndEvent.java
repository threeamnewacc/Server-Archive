package net.badlion.tournament.events.tournament;

import net.badlion.tournament.events.TournamentEvent;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.event.HandlerList;

public class TournamentEndEvent extends TournamentEvent {

    private static final HandlerList handlers = new HandlerList();

    public TournamentEndEvent(Tournament tournament) {
        super(tournament);
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
