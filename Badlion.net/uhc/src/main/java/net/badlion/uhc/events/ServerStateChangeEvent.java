package net.badlion.uhc.events;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerStateChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private BadlionUHC.BadlionUHCState oldState;
    private BadlionUHC.BadlionUHCState newState;

    public ServerStateChangeEvent(BadlionUHC.BadlionUHCState oldState, BadlionUHC.BadlionUHCState newState) {
        super();

        this.oldState = oldState;
        this.newState = newState;
    }

    public BadlionUHC.BadlionUHCState getOldState() {
        return oldState;
    }

    public BadlionUHC.BadlionUHCState getNewState() {
        return newState;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
