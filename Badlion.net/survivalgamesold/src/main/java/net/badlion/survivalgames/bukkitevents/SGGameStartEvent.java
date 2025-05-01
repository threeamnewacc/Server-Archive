package net.badlion.survivalgames.bukkitevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SGGameStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public SGGameStartEvent() {
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
