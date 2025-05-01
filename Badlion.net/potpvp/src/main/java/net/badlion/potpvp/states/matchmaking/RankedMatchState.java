package net.badlion.potpvp.states.matchmaking;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.statemachine.GState;

public class RankedMatchState extends GState<Group> {

    public RankedMatchState() {
        super("ranked_match", "they are in a ranked match.", GroupStateMachine.getInstance());
    }

}
