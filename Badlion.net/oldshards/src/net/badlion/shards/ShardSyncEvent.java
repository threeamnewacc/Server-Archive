package net.badlion.shards;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class ShardSyncEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private String type;
    private List<String> args;

    public ShardSyncEvent(String type, List<String> args) {
        this.type = type;
        this.args = args;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public List<String> getArgs() {
        return args;
    }

    public String getType() {
        return type;
    }
}
