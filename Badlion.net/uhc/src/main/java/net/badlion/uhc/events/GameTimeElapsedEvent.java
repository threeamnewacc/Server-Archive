package net.badlion.uhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTimeElapsedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private int minutes;

    public GameTimeElapsedEvent(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
