package net.badlion.potpvp.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRatingChangeEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

    private Player player;
	private int newRating;
	private String ladder;
	private boolean party;

    public PlayerRatingChangeEvent(Player player, int newRating, String ladder, boolean party) {
        this.player = player;
		this.newRating = newRating;
		this.ladder = ladder;
		this.party = party;
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

	public String getLadder() {
		return ladder;
	}

	public boolean isParty() {
		return party;
	}

}
