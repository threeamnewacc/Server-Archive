package club.minemen.hcfactions.combat.events;

import club.minemen.hcfactions.combat.LoggerNPC;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatTagCreateEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();

	private Player player;
	private LoggerNPC loggerNPC;

	private boolean cancelled = false;

	public CombatTagCreateEvent(Player player) {
		this.player = player;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public Player getPlayer() {
		return player;
	}

	public LoggerNPC getLoggerNPC() {
		return loggerNPC;
	}

	public void setLoggerNPC(LoggerNPC loggerNPC) {
		this.loggerNPC = loggerNPC;
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

}
