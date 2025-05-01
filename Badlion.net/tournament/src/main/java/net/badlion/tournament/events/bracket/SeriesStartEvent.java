package net.badlion.tournament.events.bracket;

import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.events.TournamentEvent;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.event.HandlerList;

public class SeriesStartEvent extends TournamentEvent {

    private static final HandlerList handlers = new HandlerList();

    private SeriesNode node;

    public SeriesStartEvent(Tournament tournament, SeriesNode node) {
        super(tournament);
        this.node = node;
    }

    public SeriesNode getSeriesNode() {
        return node;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
