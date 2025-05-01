package net.badlion.arenalobby.bukkitevents;

import net.badlion.arenalobby.ladders.Ladder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RatingRetrievedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private UUID uuid;

	private double globalRating;
	private ConcurrentHashMap<Ladder, Double> ratings;

	public RatingRetrievedEvent(UUID uuid, double globalRating, ConcurrentHashMap<Ladder, Double> ratings) {
		this.uuid = uuid;

		this.globalRating = globalRating;
		this.ratings = ratings;
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public double getGlobalRating() {
		return this.globalRating;
	}

	public ConcurrentHashMap<Ladder, Double> getRatings() {
		return this.ratings;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

}
