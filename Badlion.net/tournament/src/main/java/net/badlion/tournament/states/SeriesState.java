package net.badlion.tournament.states;

import net.badlion.tournament.TournamentStateMachine;
import net.badlion.tournament.teams.Team;

public class SeriesState extends TeamState<Team> {

    public SeriesState() {
        super("series", "in a series", TournamentStateMachine.getInstance());
    }

}
