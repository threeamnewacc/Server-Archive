package net.badlion.tournament;

import net.badlion.statemachine.State;
import net.badlion.statemachine.StateMachine;
import net.badlion.tournament.states.LobbyState;
import net.badlion.tournament.states.MatchState;
import net.badlion.tournament.states.SeriesState;
import net.badlion.tournament.states.TeamState;
import net.badlion.tournament.teams.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TournamentStateMachine extends StateMachine<Team> {

    private static TournamentStateMachine stateMachine;
    private LobbyState lobbyState;
    private SeriesState seriesState;
    private MatchState matchState;

    private static final Map<UUID, Team> uuidToTeam = new HashMap<>();

    public TournamentStateMachine() {
        TournamentStateMachine.stateMachine = this;
    }

    public void init() {

        this.lobbyState = new LobbyState();
        this.seriesState = new SeriesState();
        this.matchState = new MatchState();

        // Allow teams to transition to a series and back
        this.lobbyState.addNextState(this.seriesState);
        this.seriesState.addNextState(this.lobbyState);

        // Setup Child State for series
        this.seriesState.addChildState(this.matchState);
    }

    public LobbyState getLobbyState() {
        return lobbyState;
    }

    public SeriesState getSeriesState() {
        return seriesState;
    }

    public MatchState getMatchState() {
        return matchState;
    }

    public TeamState<Team> getState(String state) {
        for (State<Team> state2 : this.getStates()) {
            if (state2.getStateName().equals(state)){
                return (TeamState<Team>)state2;
            }
        }
        return null;
    }

    public static TournamentStateMachine getInstance() {
        return TournamentStateMachine.stateMachine;
    }

    public static void storeTeam(UUID uuid, Team team) {
        TournamentStateMachine.uuidToTeam.put(uuid, team);
    }

    public static Team getTeam(UUID uuid) {
        return TournamentStateMachine.uuidToTeam.get(uuid);
    }

    public static Team getTeam(String teamString) {
        for (Team team : uuidToTeam.values()) {
            if (team.getName().equalsIgnoreCase(teamString)) {
                return team;
            }
        }
        return null;
    }

    public static void removeTeam(UUID uuid) {
        TournamentStateMachine.uuidToTeam.remove(uuid);
    }

}
