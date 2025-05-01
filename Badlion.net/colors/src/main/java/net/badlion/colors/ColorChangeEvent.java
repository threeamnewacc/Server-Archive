package net.badlion.colors;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class ColorChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private UUID uuid;

	public ColorChangeEvent(UUID uuid) {
		this.uuid = uuid;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public UUID getUuid() {
		return uuid;
	}

}
