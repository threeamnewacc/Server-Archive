package net.badlion.potpvp.states.matchmaking.events;

import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.statemachine.GState;
import org.bukkit.event.Listener;

public class WarState extends GameState implements Listener {

    public WarState() {
        super("war", "they are in a war.", GroupStateMachine.getInstance());
    }

}
