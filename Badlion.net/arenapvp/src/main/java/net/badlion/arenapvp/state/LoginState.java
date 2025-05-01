package net.badlion.arenapvp.state;

import net.badlion.arenapvp.TeamStateMachine;
import net.badlion.gberry.Gberry;
import net.badlion.statemachine.GState;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class LoginState extends GState<Player> implements Listener {

	public LoginState() {
		super("login", "they are logging in.", TeamStateMachine.getInstance());
	}

	@Override
	public void before(Player element) {
		super.before(element);
		Gberry.log("STATE", "LOGIN before: " + element.getName());
	}

	@Override
	public void after(Player player){
		super.after(player);
		Gberry.log("STATE", "LOGIN after: " + player.getName());
	}

}
