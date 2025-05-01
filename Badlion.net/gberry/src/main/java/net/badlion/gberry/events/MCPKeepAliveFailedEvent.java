package net.badlion.gberry.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.json.simple.JSONObject;

import java.util.UUID;

public class MCPKeepAliveFailedEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled = false;
	private JSONObject jsonObject;
	private UUID keepAliveId;
	private boolean mcpError;

	public MCPKeepAliveFailedEvent(JSONObject jsonObject, UUID keepAliveId, boolean mcpError) {
		this.jsonObject = jsonObject;
		this.keepAliveId = keepAliveId;
		this.mcpError = mcpError;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public UUID getKeepAliveId() {
		return keepAliveId;
	}

	public boolean isMcpError() {
		return mcpError;
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
