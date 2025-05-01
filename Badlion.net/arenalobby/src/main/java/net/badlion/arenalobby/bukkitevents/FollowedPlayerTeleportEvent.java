package net.badlion.arenalobby.bukkitevents;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class FollowedPlayerTeleportEvent extends PlayerEvent {

	private static final HandlerList handlers = new HandlerList();

	private Location location;

	public FollowedPlayerTeleportEvent(Player player) {
		super(player);
	}

	public FollowedPlayerTeleportEvent(Player player, Location location) {
		super(player);

		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
