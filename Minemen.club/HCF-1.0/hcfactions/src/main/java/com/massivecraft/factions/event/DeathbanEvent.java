package com.massivecraft.factions.event;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@RequiredArgsConstructor
public final class DeathbanEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	@Getter
	private final Player player;
	@Getter
	@Setter
	private Long deathbanTime;
	@Getter
	@Setter
	private boolean cancelled = false;

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}
}
