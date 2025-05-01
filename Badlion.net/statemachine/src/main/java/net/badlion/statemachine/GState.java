package net.badlion.statemachine;

import org.omg.SendingContext.RunTime;

import java.lang.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GState<E> implements State<E> {

    private String name = null;
    private String description = null;
    private Set<E> elements = new HashSet<>();

    private StateMachine<E> stateMachine;

    private State<E> parentState = null;
    private Set<State<E>> childrenStates = new HashSet<>();

    private Set<State<E>> nextStates = new HashSet<>();

    public GState(String name, String description, StateMachine<E> stateMachine) {
        this.name = name;
        this.description = description;
        this.stateMachine = stateMachine;
        this.stateMachine.addState(this);
    }

    /**
     * Check to see if state machines are the same
     */
    public boolean isSameStateMachine(StateMachine stateMachine) {
        return this.stateMachine == stateMachine;
    }

    /**
     * This function gets called whenever a state is being entered
     */
    public void before(E element) {
        // Do nothing here, let whoever extends this handle that
    }

    /**
     * This function gets called whenever a state is being entered
     */
    public void before(E element, Object o) {
        // Do nothing here, let whoever extends this handle that
    }

    /**
     * Transition to a new state from this state. Might throw IllegalStateException if
     * the transition is not valid
     */
    public void transition(State<E> nextState, E element) throws IllegalStateTransitionException {
        if (!this.nextStates.contains(nextState)) {
            throw new IllegalStateTransitionException(element, "Illegal transition");
        }

        if (!this.elements.contains(element)) {
            throw new MissingElementException("(Transition) This element is not found in " + this.name + " for " + element);
        }

        // Remove our element from this state
        // The only time we don't want to call the after() function is when we are going into a child state
        this.remove(element, true);

        this.stateMachine.setCurrentState(element, nextState, StateTransition.type.TRANSITION);

        // Transition to next state in the state machine
        // The only time we don't want to call the before() function is when going from a child back to a parent
        nextState.add(element, true);
    }

    /**
     * Push (go one more level into depth)
     */
    public void push(State<E> nextState, E element) throws IllegalStateTransitionException {
        this.push(nextState, element, null);
    }

    /**
     * Push (go one more level into depth)
     */
    public void push(State<E> nextState, E element, Object o) throws IllegalStateTransitionException {
        if (!this.childrenStates.contains(nextState) || nextState.getParentState() == null || nextState.getParentState() != this) {
            throw new IllegalStateTransitionException(element, "Illegal transition");
        }

        if (!this.elements.contains(element)) {
            throw new MissingElementException("(Push) This element is not found in " + this.name + " for " + element);
        }

        this.stateMachine.setCurrentState(element, nextState, StateTransition.type.PUSH);

        // Don't call after() or remove from this state's set but call before() for new state
        nextState.add(element, true, o);
    }

    /**
     * Pop (go one level out of depth)
     */
    public void pop(E element) throws IllegalStateTransitionException {
        if (this.parentState == null) {
            throw new IllegalStateTransitionException(element, "Should not be popping a state when there is no parent");
        }

        if (!this.elements.contains(element)) {
            throw new MissingElementException("(Pop) This element is not found in " + this.name + " for " + element);
        }

        this.remove(element, true);

        this.stateMachine.setCurrentState(element, this.parentState, StateTransition.type.POP);
    }

    /**
     * Pop till bottom of stack
     */
    public void popAll(E element) throws IllegalStateTransitionException {
        if (!this.elements.contains(element)) {
            throw new MissingElementException("(Popall) This element is not found in " + this.name + " for " + element);
        }

        State<E> state = this;
        while (state.hasParentState()) {
            // Let pop() call handle the after() and the state movement
            state.pop(element);

            // Get latest state from state machine
            state = this.stateMachine.getCurrentState(element);
        }
    }

    /**
     * Add object to state's list
     */
    public void add(E element, boolean callBefore) {
        this.add(element, callBefore, null);
    }

    /**
     * Add object to state's list
     */
    public void add(E element, boolean callBefore, Object o) {
        // Call before() function
        if (callBefore) {
            if (o == null) {
                this.before(element);
            } else {
                this.before(element, o);
            }
        }

        if (!this.elements.add(element)) {
            throw new RuntimeException("Failed to add " + element);
        }
    }

    /**
     * See if an element is in our set
     */
    public boolean contains(E element) {
        return this.elements.contains(element);
    }

    /**
     * Get all elements
     */
    public Set<E> elements() {
        return Collections.unmodifiableSet(this.elements);
    }

    /**
     * Remove element from state's list
     */
    public void remove(E element, boolean callAfter) {
        // Call after() function
        if (callAfter) {
            this.after(element);
        }

        if (!this.elements.remove(element)) {
            throw new RuntimeException("Failed to remove " + element);
        }
    }

    /**
     * Checks to see if a state is valid or not (possibly disabled)
     */
    public boolean isStateTransitionValid(State<E> state) {
        return (this.hasParentState() && this.parentState.equals(state)) || this.childrenStates.contains(state) || this.nextStates.contains(state);
    }

    /**
     * This function gets called whenever a state is being exited
     */
    public void after(E element) {
        // Do nothing here, let whoever extends this handle that
    }

    /**
     * Returns the state's name mapping
     */
    public String getStateName() {
        return this.name;
    }

    /**
     * Has a parent state
     */
    public boolean hasParentState() {
        return this.parentState != null;
    }

    /**
     * Get Parent State
     */
    public State<E> getParentState() { return this.parentState; }

    /**
     * Add Parent State
     *
     * WARNING: THIS SHOULD NOT BE USED ANYWHERE OUTSIDE OF THIS PACKAGE
     */
    public void setParentState(State<E> state) {
        if (state != null && !Thread.currentThread().getStackTrace()[2].getClassName().equals("net.badlion.statemachine.GState")) {
            throw new RuntimeException("Cannot set state to anything but null from outside the state.");
        }

        this.parentState = state;
    }

    /**
     * Add Child State
     */
    public void addChildState(State<E> state) {
        this.childrenStates.add(state);
        state.setParentState(this);
    }

    /**
     * Add Child State
     */
    public void addNextState(State<E> state) {
        this.nextStates.add(state);
    }

    /**
     * Get State Machine
     */
    public StateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * Description of state
     */
    public String description() {
        return this.description;
    }

    /**
     * Get Unmodifiable Elements
     */
    public Set<E> getElements() {
        return Collections.unmodifiableSet(this.elements);
    }
}
