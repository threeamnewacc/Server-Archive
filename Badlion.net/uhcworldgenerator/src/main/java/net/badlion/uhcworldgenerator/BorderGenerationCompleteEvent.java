package net.badlion.uhcworldgenerator;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BorderGenerationCompleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private String worldName;

    public BorderGenerationCompleteEvent(String worldName) {
        this.worldName = worldName;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getWorldName() {
        return worldName;
    }

}
