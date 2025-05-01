package net.badlion.arenalobby.states;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.statemachine.GState;
import org.bukkit.entity.Player;

public class LoginState extends GState<Group> {

	public LoginState() {
		super("login", "they are logging in.", GroupStateMachine.getInstance());
	}

	@Override
	public void before(Group element) {
		super.before(element);

		for (Player player : element.players()) {
			LobbyState.givePlayerItems(player);

			ArenaLobby.getInstance().healAndTeleportToSpawn(player);
		}
	}

}
