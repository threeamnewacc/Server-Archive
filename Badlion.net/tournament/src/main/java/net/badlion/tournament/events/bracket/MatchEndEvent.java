package net.badlion.tournament.events.bracket;

import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.events.TournamentEvent;
import net.badlion.tournament.matches.Round;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.event.HandlerList;

public class MatchEndEvent extends TournamentEvent {

    private static final HandlerList handlers = new HandlerList();

    private SeriesNode node;
    private Round round;
    private Team winningTeam;

    public MatchEndEvent(Tournament tournament, SeriesNode node, Round round) {
        this(tournament, node, round, null);
    }

    public MatchEndEvent(Tournament tournament, SeriesNode node, Round round, Team winningTeam) {
        super(tournament);
        this.node = node;
        this.round = round;
        this.winningTeam = winningTeam;
    }

    public SeriesNode getSeriesNode() {
        return node;
    }

    public Round getRound() {
        return round;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
