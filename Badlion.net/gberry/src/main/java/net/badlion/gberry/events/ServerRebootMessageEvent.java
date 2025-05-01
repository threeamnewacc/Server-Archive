package net.badlion.gberry.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

public class ServerRebootMessageEvent extends ServerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private int minutesLeft;

	private boolean cancelled = false;

	public ServerRebootMessageEvent(int minutesLeft) {
		this.minutesLeft = minutesLeft;
	}

	public int getMinutesLeft() {
		return minutesLeft;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancelled = b;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
