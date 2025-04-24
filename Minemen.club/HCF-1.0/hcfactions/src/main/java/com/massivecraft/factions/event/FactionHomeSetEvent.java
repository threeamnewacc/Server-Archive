package com.massivecraft.factions.event;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionHomeSetEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private FLocation location;
	private Faction faction;
	private FactionPlayer fplayer;

	public FactionHomeSetEvent(FLocation loc, Faction f, FactionPlayer p) {
		location = loc;
		faction = f;
		fplayer = p;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public FLocation getLocation() {
		return this.location;
	}

	public Faction getFaction() {
		return faction;
	}

	public String getFactionId() {
		return faction.getId();
	}

	public String getFactionTag() {
		return faction.getTag();
	}

	public FactionPlayer getFPlayer() {
		return fplayer;
	}

	public Player getPlayer() {
		return fplayer.getPlayer();
	}

}
