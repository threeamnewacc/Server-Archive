package com.massivecraft.factions.event;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionDTREvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private String id;
	private Player sender;
	private double dtr;

	public FactionDTREvent(Player sender, String factionId, double dtr) {
		cancelled = false;
		this.sender = sender;
		this.id = factionId;
		this.dtr = dtr;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public Faction getFaction() {
		return Factions.getInstance().getById(id);
	}

	public FactionPlayer getFPlayer() {
		return FPlayers.getInstance().get(sender);
	}

	public Player getPlayer() {
		return sender;
	}

	public double getDtr() {
		return dtr;
	}

	public void setDtr(double dtr) {
		this.dtr = dtr;
	}
}
