package net.badlion.statemachine;

import org.junit.Assert;
import org.junit.Test;

public class StateMachineTest {

    @Test
    public void testBasicTransitions() {
        StateMachine<Integer> stateMachine = new StateMachine<>();
        State<Integer> state1 = new GState<>("state1", "state1", stateMachine);
        State<Integer> state2 = new GState<>("state2", "state2", stateMachine);
        State<Integer> state3 = new GState<>("state3", "state3", stateMachine);

        state1.addNextState(state2);
        state2.addNextState(state3);
        state3.addNextState(state1);

        Integer elem = 1;
        state1.add(elem, true);

        try {
            state1.transition(state2, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to transition from state 1 to state 2");
        }

        try {
            state2.transition(state3, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to transition from state 2 to state 3");
        }

        try {
            state3.transition(state1, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to transition from state 3 to state 1");
        }

        Assert.assertTrue("Elem is not found in state 1", state1.contains(elem));

        try {
            state1.transition(state3, elem);
        } catch (IllegalStateTransitionException e) {
            return;
        }

        Assert.fail("Failed to stop elem from transitioning from state 1 to state 3.");
    }

    @Test
    public void testBasicDepth() {
        StateMachine<Integer> stateMachine = new StateMachine<>();
        State<Integer> state1 = new GState<>("state1", "state1", stateMachine);
        State<Integer> state2 = new GState<>("state2", "state2", stateMachine);
        State<Integer> state3 = new GState<>("state3", "state3", stateMachine);
        State<Integer> state4 = new GState<>("state4", "state4", stateMachine);
        State<Integer> state5 = new GState<>("state5", "state5", stateMachine);

        state1.addNextState(state2);
        state1.addChildState(state4);
        state4.addChildState(state5);

        state2.addNextState(state3);
        state3.addNextState(state1);

        Integer elem = 1;
        state1.add(elem, true);

        try {
            state1.push(state4, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to push elem from state 1 to start 4");
        }

        State<Integer> currentState = stateMachine.getCurrentState(elem);
        Assert.assertEquals("Current State and state4 are not equal", currentState, state4);

        try {
            state4.push(state5, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to push elem from state 1 to start 4");
        }

        // Quickly validate that each of the states has the element
        Assert.assertTrue("State1 does not contain elem", state1.contains(elem));
        Assert.assertTrue("State4 does not contain elem", state4.contains(elem));
        Assert.assertTrue("State5 does not contain elem", state5.contains(elem));
        Assert.assertFalse("State2 contains elem", state2.contains(elem));
        Assert.assertFalse("State3 contains elem", state3.contains(elem));

        // Try to get back to state1
        try {
            state5.popAll(elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to popall from state5");
        }

        // Quickly validate that each of the states has the element
        Assert.assertTrue("State1 does not contain elem", state1.contains(elem));
        Assert.assertFalse("State4 contains elem", state4.contains(elem));
        Assert.assertFalse("State5 contains elem", state5.contains(elem));
        Assert.assertFalse("State2 contains elem", state2.contains(elem));
        Assert.assertFalse("State3 contains elem", state3.contains(elem));

        // Try to clean it up
        stateMachine.cleanupElement(elem);

        Assert.assertFalse("State1 contains elem", state1.contains(elem));
        Assert.assertNull("elem still exists in the state machine", stateMachine.getCurrentState(elem));
    }

    /**
     * Emulate an arenapvp server starting off with the lobby state, transitioning to matchmaking, and pushing
     * regular and ranked states.
     */
    @Test
    public void testEmulateArenaMatchmaking() {
        StateMachine<Integer> stateMachine = new StateMachine<>();
        State<Integer> lobbyState = new GState<>("lobby", "lobby", stateMachine);
        State<Integer> matchmakingState = new GState<>("matchmaking", "matchmaking", stateMachine);
        State<Integer> regularMatchState = new GState<>("regularmatch", "regularmatch", stateMachine);
        State<Integer> rankedMatchState = new GState<>("rankedmatch", "rankedmatch", stateMachine);

        lobbyState.addNextState(matchmakingState);
        matchmakingState.addChildState(regularMatchState);
        regularMatchState.addChildState(rankedMatchState);
        matchmakingState.addNextState(lobbyState);

        Integer elem = 1;
        lobbyState.add(elem, true);

        try {
            lobbyState.transition(matchmakingState, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to transition elem from lobby to matchmaking");
        }

        // Validate
        State<Integer> currentState = stateMachine.getCurrentState(elem);
        Assert.assertEquals("Current State and matchmaking are not equal", currentState, matchmakingState);
        Assert.assertFalse("lobby contains elem", lobbyState.contains(elem));
        Assert.assertFalse("regularmatch contains elem", regularMatchState.contains(elem));
        Assert.assertFalse("rankedmatch contains elem", rankedMatchState.contains(elem));

        try {
            matchmakingState.push(regularMatchState, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to transition elem from matchmaking to regularmatch");
        }

        // Validate
        currentState = stateMachine.getCurrentState(elem);
        Assert.assertEquals("Current State and regularmatch are not equal", currentState, regularMatchState);
        Assert.assertFalse("lobby contains elem", lobbyState.contains(elem));
        Assert.assertTrue("matchmaking contains elem", matchmakingState.contains(elem));
        Assert.assertFalse("rankedmatch contains elem", rankedMatchState.contains(elem));

        try {
            regularMatchState.push(rankedMatchState, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to transition elem from regularmatch to rankedmatch");
        }

        // Validate
        currentState = stateMachine.getCurrentState(elem);
        Assert.assertEquals("Current State and rankedmatch are not equal", currentState, rankedMatchState);
        Assert.assertFalse("lobby contains elem", lobbyState.contains(elem));
        Assert.assertTrue("matchmaking contains elem", matchmakingState.contains(elem));
        Assert.assertTrue("regularmatch contains elem", regularMatchState.contains(elem));

        // Clean up
        try {
            rankedMatchState.popAll(elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to popall from rankedstate");
        }

        try {
            matchmakingState.transition(lobbyState, elem);
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Failed to transition elem from matchmaking to lobby");
        }

        // Validate
        currentState = stateMachine.getCurrentState(elem);
        Assert.assertEquals("Current State and lobbystate are not equal", currentState, lobbyState);
        Assert.assertTrue("lobby contains elem", lobbyState.contains(elem));
        Assert.assertFalse("matchmaking contains elem", matchmakingState.contains(elem));
        Assert.assertFalse("regularmatch contains elem", regularMatchState.contains(elem));

        // What happens if we go straight to a child state of something else? This should fail
        try {
            lobbyState.transition(regularMatchState, elem);
            Assert.fail("Illegal transition from lobby to regularmatch");
        } catch (IllegalStateTransitionException e) {

        }

        // What happens if we go straight to a child state of something else? This should fail
        try {
            lobbyState.push(regularMatchState, elem);
            Assert.fail("Illegal push from lobby to regularmatch");
        } catch (IllegalStateTransitionException e) {

        }

        // What happens if we try to transition/push/pop/popall from a wrong state
        try {
            matchmakingState.transition(lobbyState, elem);
            Assert.fail("Illegal transition with no existing element");
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Illegal transition with no existing element");
        } catch (MissingElementException e) {

        }

        try {
            matchmakingState.push(regularMatchState, elem);
            Assert.fail("Illegal transition with no existing element");
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Illegal transition with no existing element");
        } catch (MissingElementException e) {

        }

        try {
            rankedMatchState.pop(elem);
            Assert.fail("Illegal transition with no existing element");
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Illegal transition with no existing element");
        } catch (MissingElementException e) {

        }

        try {
            rankedMatchState.popAll(elem);
            Assert.fail("Illegal transition with no existing element");
        } catch (IllegalStateTransitionException e) {
            Assert.fail("Illegal transition with no existing element");
        } catch (MissingElementException e) {

        }
    }


}
