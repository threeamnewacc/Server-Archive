package net.badlion.potpvp.states.matchmaking.events;

import net.badlion.potpvp.Game;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.managers.RespawnManager;
import net.badlion.potpvp.states.matchmaking.GameState;
import org.bukkit.event.Listener;

public class SlaughterState extends GameState implements Listener {

    public SlaughterState() {
        super("slaughter_state", "they are in a slaughter match.", GroupStateMachine.getInstance());
    }

	@Override
	public void before(Group group, Object o) {
		super.before(group, o);

		// Hide respawning players
		RespawnManager.handlePlayerRespawningVisibility((Game) o, group, true);
	}

	@Override
	public void after(Group group) {
		// Show respawning players
		RespawnManager.handlePlayerRespawningVisibility(GameState.getGroupGame(group), group, false);

		// Call after, we need the group game above
		super.after(group);
	}

}
