package net.badlion.tournament.states;

import net.badlion.tournament.TournamentStateMachine;
import net.badlion.tournament.teams.Team;

public class MatchState extends TeamState<Team> {

    public MatchState() {
        super("match", "in a match", TournamentStateMachine.getInstance());
    }

}
