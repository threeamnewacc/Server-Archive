package net.badlion.potpvp;

import net.badlion.potpvp.states.DuelRequestState;
import net.badlion.potpvp.states.LobbyState;
import net.badlion.potpvp.states.LoginState;
import net.badlion.potpvp.states.kits.KitCreationState;
import net.badlion.potpvp.states.matchmaking.*;
import net.badlion.potpvp.states.matchmaking.events.*;
import net.badlion.potpvp.states.party.PartyRequestState;
import net.badlion.potpvp.states.party.PartyState;
import net.badlion.potpvp.states.spectator.FollowState;
import net.badlion.potpvp.states.spectator.SpectatorState;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import net.badlion.statemachine.StateMachine;

public class GroupStateMachine extends StateMachine<Group> {

    private static GroupStateMachine groupStateMachine;

    public static LoginState loginState;
    public static LobbyState lobbyState;
	public static PartyState partyState;

    public static KitCreationState kitCreationState;

    public static SpectatorState spectatorState;
    public static FollowState followState;

	public static DuelRequestState duelRequestState;
	public static PartyRequestState partyRequestState;

	public static MatchMakingState matchMakingState;

    public static RegularMatchState regularMatchState;
    public static RankedMatchState rankedMatchState;
    public static WarState warState;
    public static LMSState lmsState;
    public static UHCMeetupState uhcMeetupState;
    public static SlaughterState slaughterState;
    public static KOTHState kothState;

    public static FFAState ffaState;

	public static TDMState tdmState;

    public GroupStateMachine() {
        GroupStateMachine.groupStateMachine = this;

	    // Create all states here to make sure stateMachine variable is created beforehand
        GroupStateMachine.loginState = new LoginState();
	    GroupStateMachine.lobbyState = new LobbyState();
	    GroupStateMachine.partyState = new PartyState();

	    GroupStateMachine.kitCreationState = new KitCreationState();

	    GroupStateMachine.spectatorState = new SpectatorState();
	    GroupStateMachine.followState = new FollowState();

	    GroupStateMachine.duelRequestState = new DuelRequestState();
	    GroupStateMachine.partyRequestState = new PartyRequestState();

	    GroupStateMachine.matchMakingState = new MatchMakingState();

        GroupStateMachine.regularMatchState = new RegularMatchState();
	    GroupStateMachine.rankedMatchState = new RankedMatchState();
        GroupStateMachine.warState = new WarState();
        GroupStateMachine.lmsState = new LMSState();
        GroupStateMachine.uhcMeetupState = new UHCMeetupState();
        GroupStateMachine.slaughterState = new SlaughterState();
        GroupStateMachine.kothState = new KOTHState();

        GroupStateMachine.ffaState = new FFAState();

	    GroupStateMachine.tdmState = new TDMState();

        // Login only one way
        GroupStateMachine.loginState.addNextState(GroupStateMachine.lobbyState);

        // Lobby
        GroupStateMachine.lobbyState.addNextState(GroupStateMachine.partyState);
        GroupStateMachine.lobbyState.addNextState(GroupStateMachine.partyRequestState);
        GroupStateMachine.lobbyState.addNextState(GroupStateMachine.spectatorState);
        GroupStateMachine.lobbyState.addNextState(GroupStateMachine.duelRequestState);
        GroupStateMachine.lobbyState.addNextState(GroupStateMachine.kitCreationState);
        GroupStateMachine.lobbyState.addNextState(GroupStateMachine.matchMakingState);
        GroupStateMachine.lobbyState.addNextState(GroupStateMachine.lobbyState); // Here cuz of stasis Manager

        // Kit Creation
        GroupStateMachine.kitCreationState.addNextState(GroupStateMachine.lobbyState);

        // Spectator
        GroupStateMachine.spectatorState.addChildState(GroupStateMachine.followState);
        //GroupStateMachine.spectatorState.addNextState(GroupStateMachine.partyState); // Party spectate was never built
        GroupStateMachine.spectatorState.addNextState(GroupStateMachine.lobbyState);

        // Party
        //GroupStateMachine.partyState.addNextState(GroupStateMachine.spectatorState); // Party spectate was never built
        GroupStateMachine.partyState.addNextState(GroupStateMachine.matchMakingState);
        GroupStateMachine.partyState.addNextState(GroupStateMachine.duelRequestState);
        //Cannot go to lobby state because they make a new group

        // Duels
        GroupStateMachine.duelRequestState.addNextState(GroupStateMachine.lobbyState);
        GroupStateMachine.duelRequestState.addNextState(GroupStateMachine.matchMakingState);
        GroupStateMachine.duelRequestState.addNextState(GroupStateMachine.partyState);

        // Party Request
        GroupStateMachine.partyRequestState.addNextState(GroupStateMachine.lobbyState);
        // Cannot go from Party Request to Party state because we add them to the other person's group

        // Match States
        GroupStateMachine.regularMatchState.addChildState(GroupStateMachine.rankedMatchState);
        GroupStateMachine.regularMatchState.addChildState(GroupStateMachine.warState);

        // Matchmaking
        GroupStateMachine.matchMakingState.addNextState(GroupStateMachine.lobbyState);
        GroupStateMachine.matchMakingState.addNextState(GroupStateMachine.partyState);
        GroupStateMachine.matchMakingState.addChildState(GroupStateMachine.regularMatchState);
        GroupStateMachine.matchMakingState.addChildState(GroupStateMachine.ffaState);
        GroupStateMachine.matchMakingState.addChildState(GroupStateMachine.tdmState);
        GroupStateMachine.matchMakingState.addChildState(GroupStateMachine.lmsState);
        GroupStateMachine.matchMakingState.addChildState(GroupStateMachine.uhcMeetupState);
        GroupStateMachine.matchMakingState.addChildState(GroupStateMachine.slaughterState);
        GroupStateMachine.matchMakingState.addChildState(GroupStateMachine.kothState);
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
            if (group.isParty()) {
                currentState.transition(GroupStateMachine.partyState, group);
            } else {
                currentState.transition(GroupStateMachine.lobbyState, group);
            }
        }
    }

}
