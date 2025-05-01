package net.badlion.mpg.bukkitevents;

import net.badlion.mpg.MPGPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MPGPlayerStateChangeEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private MPGPlayer mpgPlayer;
    private MPGPlayer.PlayerState newState;

    public MPGPlayerStateChangeEvent(MPGPlayer mpgPlayer, MPGPlayer.PlayerState newState) {
        this.mpgPlayer = mpgPlayer;
        this.newState = newState;
    }

    public MPGPlayer getMPGPlayer() {
        return mpgPlayer;
    }

    public MPGPlayer.PlayerState getCurrentState() {
        return mpgPlayer.getState();
    }

    public MPGPlayer.PlayerState getNewState() {
        return newState;
    }

    public void setNewState(MPGPlayer.PlayerState newState) {
        this.newState = newState;
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

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
