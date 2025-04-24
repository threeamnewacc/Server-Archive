package com.massivecraft.factions.event;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FPlayerKickEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	FactionPlayer issuer;
	FactionPlayer FactionPlayer;
	Faction Faction;
	boolean cancelled = false;

	public FPlayerKickEvent(FactionPlayer issuer, FactionPlayer p, Faction f) {
		this.issuer = issuer;
		FactionPlayer = p;
		Faction = f;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}


	public FactionPlayer getIssuer() {
		return issuer;
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
		cancelled = c;
	}

}
