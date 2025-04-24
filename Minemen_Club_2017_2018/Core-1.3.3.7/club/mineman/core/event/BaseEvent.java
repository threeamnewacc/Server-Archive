// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.event;

import org.bukkit.event.Cancellable;
import club.mineman.core.CorePlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class BaseEvent extends Event
{
    private static final HandlerList handlers;
    
    public static HandlerList getHandlerList() {
        return BaseEvent.handlers;
    }
    
    public HandlerList getHandlers() {
        return BaseEvent.handlers;
    }
    
    public boolean call() {
        CorePlugin.getInstance().getServer().getPluginManager().callEvent((Event)this);
        return this instanceof Cancellable && ((Cancellable)this).isCancelled();
    }
    
    static {
        handlers = new HandlerList();
    }
}
