package com.massivecraft.factions.event;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ProtectionTimeCountdownEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	@Getter
	private final Player player;
	@Getter
	@Setter
	private boolean cancelled = false;
	@Getter
	private long secondsRemaining;

	public ProtectionTimeCountdownEvent(Player player, long secondsRemaining) {
		this.player = player;
		this.secondsRemaining = secondsRemaining;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
