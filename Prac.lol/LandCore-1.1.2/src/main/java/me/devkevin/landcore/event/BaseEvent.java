package me.devkevin.landcore.event;

import me.devkevin.landcore.LandCore;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 16/01/2023 @ 12:36
 * BaseEvent / land.pvp.core.event / LandCore
 */
public class BaseEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public boolean call() {
        LandCore.getInstance().getServer().getPluginManager().callEvent(this);
        return this instanceof Cancellable && ((Cancellable) this).isCancelled();
    }

}
