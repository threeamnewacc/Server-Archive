package com.massivecraft.factions.event;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FPlayerJoinEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	FactionPlayer fplayer;
	Faction faction;
	PlayerJoinReason reason;
	boolean cancelled = false;

	public FPlayerJoinEvent(FactionPlayer fp, Faction f, PlayerJoinReason r) {
		fplayer = fp;
		faction = f;
		reason = r;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public FactionPlayer getFPlayer() {
		return fplayer;
	}

	public Faction getFaction() {
		return faction;
	}

	public PlayerJoinReason getReason() {
		return reason;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean c) {
		cancelled = c;
	}

	public enum PlayerJoinReason {

		CREATE, LEADER, COMMAND
	}
}
