package net.badlion.arenalobby;


import net.badlion.arenalobby.states.LobbyState;
import net.badlion.arenalobby.states.LoginState;
import net.badlion.arenalobby.states.kits.KitCreationState;
import net.badlion.arenalobby.states.matchmaking.MatchMakingState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import net.badlion.statemachine.StateMachine;

public class GroupStateMachine extends StateMachine<Group> {

	private static GroupStateMachine groupStateMachine;

	public static LoginState loginState;
	public static LobbyState lobbyState;

	public static KitCreationState kitCreationState;

	public static MatchMakingState matchMakingState;


	public GroupStateMachine() {
		GroupStateMachine.groupStateMachine = this;

		// Create all states here to make sure stateMachine variable is created beforehand
		GroupStateMachine.loginState = new LoginState();
		GroupStateMachine.lobbyState = new LobbyState();

		GroupStateMachine.kitCreationState = new KitCreationState();


		GroupStateMachine.matchMakingState = new MatchMakingState();

		// Login only one way
		GroupStateMachine.loginState.addNextState(GroupStateMachine.lobbyState);

		// Lobby
		GroupStateMachine.lobbyState.addNextState(GroupStateMachine.kitCreationState);
		GroupStateMachine.lobbyState.addNextState(GroupStateMachine.matchMakingState);
		GroupStateMachine.lobbyState.addNextState(GroupStateMachine.lobbyState); // Here cuz of stasis Manager

		// Kit Creation
		GroupStateMachine.kitCreationState.addNextState(GroupStateMachine.lobbyState);

		// Matchmaking
		GroupStateMachine.matchMakingState.addNextState(GroupStateMachine.lobbyState);
	}

	public static GroupStateMachine getInstance() {
		return GroupStateMachine.groupStateMachine;
	}

	public static void transitionBackToDefaultState(State<Group> currentState, Group group) throws IllegalStateTransitionException {
		// Pop any states if need be
		if (currentState.hasParentState()) {
			currentState.popAll(group);
		}

		// Get new state if we popped anything
		currentState = GroupStateMachine.getInstance().getCurrentState(group);

		// In theory this should never happen....but fuck it too much risk involved >_>
		if (currentState != null) {
			currentState.transition(GroupStateMachine.lobbyState, group);
		}
	}

}
