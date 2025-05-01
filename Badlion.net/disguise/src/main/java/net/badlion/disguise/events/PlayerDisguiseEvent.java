package net.badlion.disguise.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerDisguiseEvent extends PlayerEvent implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
	private boolean fromCommand;

	private String disguiseName;

    public PlayerDisguiseEvent(Player player, String disguiseName, boolean fromCommand) {
        super(player);

	    this.disguiseName = disguiseName;
	    this.fromCommand = fromCommand;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

	public boolean isFromCommand() {
		return this.fromCommand;
	}

	public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

	public String getDisguiseName() {
		return this.disguiseName;
	}

}
