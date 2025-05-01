package net.badlion.tournament.events;

import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TournamentEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Tournament tournament;

    public TournamentEvent(Tournament tournament) {
        this.tournament = tournament;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
