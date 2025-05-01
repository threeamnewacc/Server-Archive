package net.badlion.gcheat.bukkitevents;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import java.util.Map;

public class GCheatGameEndEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

	private Map<String, Object> data;

	public GCheatGameEndEvent(Player player) {
		super(player);
	}

	public GCheatGameEndEvent(Player player, Map<String, Object> data) {
		super(player);

		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
