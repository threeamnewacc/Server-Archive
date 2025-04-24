package me.devkevin.landcore.event.server;

import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

public class ServerShutdownCancelEvent extends ServerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
