package net.badlion.mpg.bukkitevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MPGGameTimeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	private long time;

	public MPGGameTimeEvent(long time) {
		this.time = time;
	}
	public HandlerList getHandlers() {
		return handlers;
	}

	public long getTime() {
		return time;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
