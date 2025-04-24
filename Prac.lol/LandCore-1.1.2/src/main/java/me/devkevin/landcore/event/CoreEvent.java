package me.devkevin.landcore.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/01/2023 @ 18:31
 * CoreEvent / me.devkevin.landcore.event / LandCore
 */
public class CoreEvent extends Event {

    private static HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

