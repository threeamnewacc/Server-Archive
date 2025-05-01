package net.badlion.potpvp.bukkitevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class TDMCounterFilledEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private UUID uuid;

    public TDMCounterFilledEvent(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
