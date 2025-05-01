package net.badlion.arenapvp;

import net.badlion.arenapvp.state.DeathState;
import net.badlion.arenapvp.state.FollowState;
import net.badlion.arenapvp.state.LoginState;
import net.badlion.arenapvp.state.MatchState;
import net.badlion.arenapvp.state.RedRoverWaitingState;
import net.badlion.arenapvp.state.SpectatorState;
import net.badlion.statemachine.StateMachine;
import org.bukkit.entity.Player;

public class TeamStateMachine extends StateMachine<Player> {

	private static TeamStateMachine teamStateMachine;

	public static LoginState loginState;
	public static MatchState matchState;

	public static DeathState deathState;

	public static SpectatorState spectatorState;
	public static FollowState followState;

	public static RedRoverWaitingState redRoverWaitingState;

	public TeamStateMachine() {
		TeamStateMachine.teamStateMachine = this;

		// Create all states here to make sure stateMachine variable is created beforehand
		TeamStateMachine.loginState = new LoginState();
		TeamStateMachine.matchState = new MatchState();
		TeamStateMachine.spectatorState = new SpectatorState();
		TeamStateMachine.followState = new FollowState();
		TeamStateMachine.deathState = new DeathState();
		TeamStateMachine.redRoverWaitingState = new RedRoverWaitingState();

		// Login, Spectator or match
		TeamStateMachine.loginState.addNextState(TeamStateMachine.spectatorState);
		TeamStateMachine.loginState.addNextState(TeamStateMachine.matchState);

		// Match goes into spectator after they die or game ends
		TeamStateMachine.matchState.addNextState(TeamStateMachine.spectatorState);
		TeamStateMachine.matchState.addChildState(TeamStateMachine.redRoverWaitingState);
		TeamStateMachine.matchState.addNextState(TeamStateMachine.deathState);

		TeamStateMachine.redRoverWaitingState.addNextState(TeamStateMachine.matchState);
		TeamStateMachine.redRoverWaitingState.addNextState(TeamStateMachine.spectatorState);

		TeamStateMachine.deathState.addNextState(TeamStateMachine.matchState);

		TeamStateMachine.spectatorState.addNextState(TeamStateMachine.matchState);
		TeamStateMachine.spectatorState.addChildState(TeamStateMachine.followState);
		TeamStateMachine.spectatorState.addNextState(TeamStateMachine.deathState);

	}

	public static TeamStateMachine getInstance() {
		return TeamStateMachine.teamStateMachine;
	}


}
