package net.badlion.mpg.bukkitevents.teams;

import net.badlion.mpg.MPGPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamLeaveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private MPGPlayer mpgPlayer;

    public TeamLeaveEvent(MPGPlayer mpgPlayer) {
        this.mpgPlayer = mpgPlayer;
    }

    public MPGPlayer getMPGPlayer() {
        return this.mpgPlayer;
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
