package net.badlion.statemachine;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StateTransition {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss.SSS zzz yyyy");

    public enum type { MANUAL, TRANSITION, PUSH, POP }

    private State state;
    private StateTransition.type type;
    private Date datetime;

    public StateTransition(State state, StateTransition.type type, Date datetime) {
        this.state = state;
        this.type = type;
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "[" + dateFormat.format(this.datetime) + "] " + this.state.getStateName() + " (" + type.name() + ")";
    }

}
