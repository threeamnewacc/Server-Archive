package net.badlion.tournament.events.team;

import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.events.TournamentEvent;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.event.HandlerList;

public class TeamReadyEvent extends TournamentEvent {

    private static final HandlerList handlers = new HandlerList();

    private SeriesNode node;
    private Team team;

    public TeamReadyEvent(Tournament tournament, SeriesNode node, Team team) {
        super(tournament);
        this.node = node;
        this.team = team;
    }

    public SeriesNode getSeriesNode() {
        return node;
    }

    public Team getTeam() {
        return team;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
