package com.massivecraft.factions.event;

import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public class FactionPostCreateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final Player player;
	private final Faction faction;


	public static HandlerList getHandlerList() {
		return handlers;
	}

	public FactionPlayer getFPlayer() {
		return FPlayers.getInstance().get(player);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
}