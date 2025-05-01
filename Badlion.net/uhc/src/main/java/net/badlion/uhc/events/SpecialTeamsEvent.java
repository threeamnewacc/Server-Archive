package net.badlion.uhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpecialTeamsEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private boolean overriden = false;

    public boolean isOverriden() {
        return overriden;
    }

    public void setOverriden(boolean overriden) {
        this.overriden = overriden;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
