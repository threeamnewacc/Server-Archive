package net.badlion.disguise.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerUndisguiseEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
	private boolean fromCommand;

    public PlayerUndisguiseEvent(Player player, boolean fromCommand) {
        super(player);

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

}
