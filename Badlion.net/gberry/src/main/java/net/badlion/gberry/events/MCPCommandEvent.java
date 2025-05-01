package net.badlion.gberry.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MCPCommandEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private String typeOfSync;
    private String command;

    public MCPCommandEvent(String typeOfSync, String command) {
        this.typeOfSync = typeOfSync;
        this.command = command;
    }

    public String getTypeOfSync() {
        return typeOfSync;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
