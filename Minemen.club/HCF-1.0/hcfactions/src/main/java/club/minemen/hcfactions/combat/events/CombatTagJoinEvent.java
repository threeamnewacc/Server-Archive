package club.minemen.hcfactions.combat.events;

import club.minemen.hcfactions.combat.LoggerNPC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatTagJoinEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private LoggerNPC loggerNPC;

	public CombatTagJoinEvent(LoggerNPC loggerNPC) {
		this.loggerNPC = loggerNPC;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	public LoggerNPC getLoggerNPC() {
		return loggerNPC;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

}
