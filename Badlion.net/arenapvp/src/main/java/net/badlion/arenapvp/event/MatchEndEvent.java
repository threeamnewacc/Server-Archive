package net.badlion.arenapvp.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

public class MatchEndEvent extends Event {

	private static final HandlerList handlers = new HandlerList();


	private List<Player> players = new ArrayList<>();

	public MatchEndEvent(List<Player> players) {
		this.players = players;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
