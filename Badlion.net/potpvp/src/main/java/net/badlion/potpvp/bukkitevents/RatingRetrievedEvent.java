package net.badlion.potpvp.bukkitevents;

import net.badlion.potpvp.ladders.Ladder;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RatingRetrievedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private UUID uuid;
    private ConcurrentHashMap<Ladder, Integer> ratings;

    public RatingRetrievedEvent(UUID uuid, ConcurrentHashMap<Ladder, Integer> ratings) {
        this.uuid = uuid;
        this.ratings = ratings;
    }

    public UUID getUuid() {
        return uuid;
    }

    public ConcurrentHashMap<Ladder, Integer> getRatings() {
        return ratings;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
