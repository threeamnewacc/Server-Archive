package net.badlion.mpg.bukkitevents.teams;

import net.badlion.mpg.MPGPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TeamKickEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private MPGPlayer kicker;
    private MPGPlayer kicked;

    public TeamKickEvent(MPGPlayer kicker, MPGPlayer kicked) {
        this.kicker = kicker;
        this.kicked = kicked;
    }

    public MPGPlayer getKicker() {
        return this.kicker;
    }

    public MPGPlayer getKicked() {
        return kicked;
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
