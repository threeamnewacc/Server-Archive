package com.massivecraft.factions.event;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FPlayerLeaveEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	FactionPlayer FactionPlayer;
	Faction Faction;
	boolean cancelled = false;
	private PlayerLeaveReason reason;

	public FPlayerLeaveEvent(FactionPlayer p, Faction f, PlayerLeaveReason r) {
		FactionPlayer = p;
		Faction = f;
		reason = r;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public PlayerLeaveReason getReason() {
		return reason;
	}

	public FactionPlayer getFactionPlayer() {
		return FactionPlayer;
	}

	public Faction getFaction() {
		return Faction;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean c) {
		if (reason == PlayerLeaveReason.DISBAND || reason == PlayerLeaveReason.RESET) {
			cancelled = false;
			return;
		}
		cancelled = c;
	}

	public enum PlayerLeaveReason {

		KICKED, DISBAND, RESET, JOINOTHER, LEAVE
	}
}
