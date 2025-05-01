package net.badlion.combattag.events;

import net.badlion.combattag.LoggerNPC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CombatTagDestroyEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private LoggerNPC loggerNPC;
    private LoggerNPC.REMOVE_REASON reason;

    public CombatTagDestroyEvent(LoggerNPC loggerNPC, LoggerNPC.REMOVE_REASON reason) {
        this.loggerNPC = loggerNPC;
        this.reason = reason;
    }

    public LoggerNPC getLoggerNPC() {
        return loggerNPC;
    }

    public LoggerNPC.REMOVE_REASON getReason() {
        return reason;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
