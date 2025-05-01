package net.badlion.smellycases.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RequestPlayerOwnedCases extends Event {

	private static final HandlerList handlers = new HandlerList();
	public static Map<UUID, Set<String>> playerOwnedCases = new LinkedHashMap<>();
	private Player player;

	public RequestPlayerOwnedCases(Player player) {
		super(false);

		this.player = player;
		RequestPlayerOwnedCases.playerOwnedCases.put(this.player.getUniqueId(), new HashSet<String>());
	}

	public static boolean hasCaseItem(Player player, String string) {
		Set<String> ownedCases = playerOwnedCases.get(player.getUniqueId());
		return ownedCases != null && ownedCases.contains(string);
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public void addCase(String string) {
		RequestPlayerOwnedCases.playerOwnedCases.get(this.player.getUniqueId()).add(string);
	}
}
