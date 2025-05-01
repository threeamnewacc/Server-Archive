package net.badlion.survivalgames.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRatingChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

    private Player player;
	private int newRating;
	private int newGlobalRating;
	private String ladder;

    public PlayerRatingChangeEvent(Player player, int newRating, int newGlobalRating, String ladder) {
        this.player = player;
		this.newRating = newRating;
		this.newGlobalRating = newGlobalRating;
		this.ladder = ladder;
    }

    public Player getPlayer() {
        return player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

	public int getNewRating() {
		return newRating;
	}

	public int getNewGlobalRating() {
		return newGlobalRating;
	}

	public String getLadder() {
		return ladder;
	}

}
