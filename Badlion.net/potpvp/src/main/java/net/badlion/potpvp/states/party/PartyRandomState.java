package net.badlion.potpvp.states.party;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.statemachine.GState;

public class PartyRandomState extends GState<Group> {

    public PartyRandomState() {
        super("party", "they have a party request.", GroupStateMachine.getInstance());
    }

}
