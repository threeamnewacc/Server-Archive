package net.badlion.gberry.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HelpCommandEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private String[] args;

	public HelpCommandEvent(Player player, String[] args) {
		this.player = player;
		this.args = args;
	}

	public Player getPlayer() {
		return player;
	}

	public String[] getArgs() {
		return args;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
