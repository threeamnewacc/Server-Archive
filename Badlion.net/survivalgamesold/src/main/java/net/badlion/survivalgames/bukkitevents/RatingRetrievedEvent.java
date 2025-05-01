package net.badlion.survivalgames.bukkitevents;

import net.badlion.survivalgames.Ladder;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.concurrent.ConcurrentHashMap;


public class RatingRetrievedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private ConcurrentHashMap<Ladder, Integer> ratings;
	private int numOfMatchesToday;

    public RatingRetrievedEvent(Player player, ConcurrentHashMap<Ladder, Integer> ratings, int numOfMatchesToday) {
        this.player = player;
        this.ratings = ratings;
	    this.numOfMatchesToday = numOfMatchesToday;
    }

	public Player getPlayer() {
		return player;
	}

	public ConcurrentHashMap<Ladder, Integer> getRatings() {
        return ratings;
    }

	public int getNumOfMatchesToday() {
		return numOfMatchesToday;
	}

	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
