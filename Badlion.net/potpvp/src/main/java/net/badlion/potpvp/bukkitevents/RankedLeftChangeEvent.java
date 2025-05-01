package net.badlion.potpvp.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RankedLeftChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

    private Player player;

	private int rankedLeft;

    public RankedLeftChangeEvent(Player player, int rankedLeft) {
        this.player = player;
		this.rankedLeft = rankedLeft;
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

}
