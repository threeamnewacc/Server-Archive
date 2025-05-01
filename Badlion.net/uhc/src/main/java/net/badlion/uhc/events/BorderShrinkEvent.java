package net.badlion.uhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BorderShrinkEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private boolean override;
    private int oldRadius;
    private int newRadius;

    public BorderShrinkEvent(int oldRadius, int newRadius) {
        super();

        this.oldRadius = oldRadius;
        this.newRadius = newRadius;
        this.override = false;
    }

    public boolean isOverride() {
        return override;
    }

    public void setOverride(boolean override) {
        this.override = override;
    }

    public int getOldRadius() {
        return oldRadius;
    }

    public int getNewRadius() {
        return newRadius;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
