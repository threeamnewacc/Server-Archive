package net.badlion.worldborder;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldBorderFillMessageEvent  extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String message;

    public WorldBorderFillMessageEvent(String message) {
        this.message = message;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getMessage() {
        return message;
    }
}
