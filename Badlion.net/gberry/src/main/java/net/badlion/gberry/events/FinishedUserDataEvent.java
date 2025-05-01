package net.badlion.gberry.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class FinishedUserDataEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private UUID uuid;

	public FinishedUserDataEvent(UUID uuid) {
		super(false);

		this.uuid = uuid;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
