package net.badlion.combattag.events;

import net.badlion.combattag.LoggerNPC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatTagKilledEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private LoggerNPC loggerNPC;

    public CombatTagKilledEvent(LoggerNPC loggerNPC) {
        this.loggerNPC = loggerNPC;
    }

    public LoggerNPC getLoggerNPC() {
        return loggerNPC;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
