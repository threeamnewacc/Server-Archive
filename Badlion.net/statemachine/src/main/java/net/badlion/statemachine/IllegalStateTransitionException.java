package net.badlion.statemachine;

import org.bukkit.Bukkit;

import java.util.List;

public class IllegalStateTransitionException extends Exception {

    public IllegalStateTransitionException(Object object, String msg) {
        super(msg);

        if (StateMachine.DEBUG) {
            List<String> lines = StateMachine.stateMachine.debugTransitionsForElement(object);
            for (String line : lines) {
                Bukkit.getLogger().info(line);
            }
        }
    }

}
