package net.badlion.mpg.bukkitevents;

import net.badlion.mpg.MPG;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MPGServerStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private MPG.ServerState oldState;
    private MPG.ServerState newState;

    public MPGServerStateChangeEvent(MPG.ServerState oldState, MPG.ServerState newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public MPG.ServerState getOldState() {
        return this.oldState;
    }

    public MPG.ServerState getNewState() {
        return this.newState;
    }

	public void setNewState(MPG.ServerState newState) {
		this.newState = newState;
	}

	public HandlerList getHandlers() {
        return MPGServerStateChangeEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return MPGServerStateChangeEvent.handlers;
    }

}
