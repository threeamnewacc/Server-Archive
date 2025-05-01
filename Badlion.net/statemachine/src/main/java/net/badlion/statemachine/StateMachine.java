package net.badlion.statemachine;

import java.util.*;

public class StateMachine<E> {

    private Set<State<E>> states = new HashSet<>();

    public static boolean DEBUG = false;
    public static StateMachine stateMachine;

    private Map<E, Queue<StateTransition>> elementToStateTransitions = new HashMap<>();
    private Map<E, State<E>> currentStateMap = new HashMap<>();

    public StateMachine() {
        StateMachine.stateMachine = this;
    }

    public void cleanupElement(E element) {
        // Cleanup whatever state they are in if one exists
        State<E> state = this.getCurrentState(element);
        if (state != null) {
            if (state.hasParentState()) {
                try {
                    state.popAll(element);
                } catch (IllegalStateTransitionException e) {
                    // Ignore it
                }
            }

            // Just incase
            state = this.getCurrentState(element);
            if (state != null) {
                state.remove(element, true);
            }
        }

        // Cleanup state machine memory
        this.elementToStateTransitions.remove(element);
        this.currentStateMap.remove(element);
    }

    public void addDebugTransition(E element, State state, StateTransition.type type) {
        StateTransition stateTransition = new StateTransition(state, type, new Date());
        Queue<StateTransition> stateTransitions = this.elementToStateTransitions.get(element);
        if (stateTransitions != null) {
            stateTransitions.add(stateTransition);
        } else {
            stateTransitions = new LinkedList<>();
            stateTransitions.add(stateTransition);

            this.elementToStateTransitions.put(element, stateTransitions);
        }
    }

    public List<String> debugTransitionsForElement(E element) {
        Queue<StateTransition> states = this.elementToStateTransitions.get(element);

        ArrayList<String> transitions = new ArrayList<>();
        transitions.add("====Transitions for " + element.toString() + "====");

        if (states.size() < 1) {
            return transitions;
        }

        Iterator<StateTransition> iterator = states.iterator();
        StateTransition state = iterator.next();

        if (state != null) {
            while (iterator.hasNext()) {
                StateTransition oldState = state;
                state = iterator.next();

                // Record if we aren't at end of Queue
                transitions.add(oldState.toString() + " --> " + state.toString());
            }
        }

        return transitions;
    }

    public void setCurrentState(E element, State<E> state) {
        setCurrentState(element, state, StateTransition.type.MANUAL);
    }

    public void setCurrentState(E element, State<E> state, StateTransition.type type) {
        if (state == null) {
            throw new RuntimeException("Cannot set state to null");
        }

        this.addDebugTransition(element, state, type);

        this.currentStateMap.put(element, state);
    }

    public State<E> getCurrentState(E element) {
        return this.currentStateMap.get(element);
    }

    public void addState(State<E> state) {
        this.states.add(state);
    }

    public Set<State<E>> getStates() {
        return states;
    }
}
