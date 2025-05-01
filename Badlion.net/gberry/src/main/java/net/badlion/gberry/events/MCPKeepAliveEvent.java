package net.badlion.gberry.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.UUID;

public class MCPKeepAliveEvent extends Event implements Cancellable {

    public enum KeepAliveType {
        SEND,
        RESPONSE
    }

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private JSONObject jsonObject;
    private KeepAliveType type;
    private UUID keepAliveId;

    public MCPKeepAliveEvent(JSONObject jsonObject, KeepAliveType type, UUID keepAliveId) {
        this.jsonObject = jsonObject;
        this.type = type;
        this.keepAliveId = keepAliveId;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public KeepAliveType getType() {
        return type;
    }

    public UUID getKeepAliveId() {
        return keepAliveId;
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
