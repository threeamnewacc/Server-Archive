package net.badlion.smellychat.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GlobalMuteEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public boolean isCancelled() {
		return this.cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

}

