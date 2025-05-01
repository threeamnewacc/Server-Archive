package net.badlion.gfactions.bukkitevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventStateChangeEvent extends Event {

    private String eventName;
    private boolean active;

    private static final HandlerList handlers = new HandlerList();

    public EventStateChangeEvent(String eventName, boolean active) {
        this.eventName = eventName;
        this.active = active;
}

    public String getEventName() {
        return eventName;
    }

    public boolean isActive() {
        return active;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
