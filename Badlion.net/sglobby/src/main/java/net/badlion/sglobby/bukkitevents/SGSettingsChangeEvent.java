package net.badlion.sglobby.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SGSettingsChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private Player player;

	private boolean ratingVisibility;
	private boolean statsVisibility;

	public SGSettingsChangeEvent(Player player, boolean ratingVisibility, boolean statsVisibility) {
		this.player = player;

		this.ratingVisibility = ratingVisibility;
		this.statsVisibility = statsVisibility;
	}

	public Player getPlayer() {
		return this.player;
	}

	public boolean isRatingVisibile() {
		return this.ratingVisibility;
	}

	public boolean areStatsVisible() {
		return this.statsVisibility;
	}

	public HandlerList getHandlers() {
		return SGSettingsChangeEvent.handlers;
	}

	public static HandlerList getHandlerList() {
		return SGSettingsChangeEvent.handlers;
	}

}
