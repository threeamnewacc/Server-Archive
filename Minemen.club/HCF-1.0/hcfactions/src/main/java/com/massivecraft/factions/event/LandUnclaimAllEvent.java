package com.massivecraft.factions.event;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LandUnclaimAllEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Faction faction;
	private FactionPlayer fplayer;

	public LandUnclaimAllEvent(Faction f, FactionPlayer p) {
		faction = f;
		fplayer = p;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
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
