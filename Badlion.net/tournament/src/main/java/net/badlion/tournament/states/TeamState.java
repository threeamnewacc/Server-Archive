package net.badlion.tournament.states;

import net.badlion.statemachine.GState;
import net.badlion.statemachine.StateMachine;
import net.badlion.tournament.teams.Team;

import java.util.HashSet;
import java.util.Set;

public class TeamState<E extends Team> extends GState<E> {

    private Set<Team> editedTeams = new HashSet<>();

    public TeamState(String name, String description, StateMachine<E> stateMachine) {
        super(name, description, stateMachine);
    }

    public boolean isEdited(Team team) {
        return editedTeams.contains(team);
    }

    public Set<Team> getEditedTeams() {
        return editedTeams;
    }

    @Override
    public void add(E element, boolean callBefore) {
        super.add(element, callBefore);
        getEditedTeams().add(element);
    }
}
