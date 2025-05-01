package net.badlion.sgrankedmatchmaker.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankedLeftChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

    private Player player;

	private int rankedLeft;
	private boolean hasMoreMatches;

    public RankedLeftChangeEvent(Player player, int rankedLeft, boolean hasMoreMatches) {
        this.player = player;
		this.rankedLeft = rankedLeft;
	    this.hasMoreMatches = hasMoreMatches;
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

	public int getRankedLeft() {
		return rankedLeft;
	}

	public boolean hasMoreMatches() {
		return hasMoreMatches;
	}

}
