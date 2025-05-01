package net.badlion.statemachine;

import java.util.Set;

public interface State<E> {

    /**
     * Check to see if state machines are the same
     */
    public boolean isSameStateMachine(StateMachine stateMachine);

    /**
     * This function gets called whenever a state is being entered
     */
    public void before(E element);

    /**
     * This function gets called whenever a state is being entered
     */
    public void before(E element, Object o);

    /**
     * Transition to a new state from this state. Might throw IllegalStateException if
     * the transition is not valid
     */
    public void transition(State<E> nextState, E element) throws IllegalStateTransitionException;

    /**
     * Push (go one more level into depth)
     */
    public void push(State<E> nextState, E element) throws IllegalStateTransitionException;

    /**
     * Push (go one more level into depth)
     */
    public void push(State<E> nextState, E element, Object o) throws IllegalStateTransitionException;

    /**
     * Pop (go one level out of depth)
     */
    public void pop(E element) throws IllegalStateTransitionException;

    /**
     * Pop till bottom of stack
     */
    public void popAll(E element) throws IllegalStateTransitionException;

    /**
     * Add object to state's list
     */
    public void add(E element, boolean callBefore);

    /**
     * Add object to state's list
     */
    public void add(E element, boolean callBefore, Object o);

    /**
     * See if an element is in our set
     */
    public boolean contains(E element);

    /**
     * Get all elements
     */
    public Set<E> elements();

    /**
     * Remove element from state's list
     */
    public void remove(E element, boolean callAfter);

    /**
     * Checks to see if a state is valid or not (possibly disabled)
     */
    public boolean isStateTransitionValid(State<E> state);

    /**
     * This function gets called whenever a state is being exited
     */
    public void after(E element);

    /**
     * Returns the state's name mapping
     */
    public String getStateName();

    /**
     * Has a parent state
     */
    public boolean hasParentState();

    /**
     * Get Parent State
     */
    public State<E> getParentState();

    /**
     * Set a parent state and adds the state to the parent's children (private method)
     */
    public void setParentState(State<E> state);

    /**
     * Add Child State
     */
    public void addChildState(State<E> state);

    /**
     * Add Next State
     */
    public void addNextState(State<E> state);

    /**
     * Get State Machine
     */
    public StateMachine getStateMachine();

    /**
     * Description of state
     */
    public String description();

    /**
     * Get Unmodifiable Elements
     */
    public Set<E> getElements();

}
