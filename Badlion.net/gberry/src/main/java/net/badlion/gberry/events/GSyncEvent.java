package net.badlion.gberry.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class GSyncEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private List<String> args;

    public GSyncEvent(List<String> args) {
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
