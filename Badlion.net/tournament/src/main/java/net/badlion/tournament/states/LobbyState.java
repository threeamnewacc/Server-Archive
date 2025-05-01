package net.badlion.tournament.states;

import net.badlion.tournament.TournamentStateMachine;
import net.badlion.tournament.teams.Team;

public class LobbyState extends TeamState<Team> {

    public LobbyState() {
        super("lobby", "waiting for a match", TournamentStateMachine.getInstance());
    }

}
