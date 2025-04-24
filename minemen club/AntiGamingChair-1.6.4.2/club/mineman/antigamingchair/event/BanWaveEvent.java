// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.event;

import java.beans.ConstructorProperties;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class BanWaveEvent extends Event
{
    private static final HandlerList HANDLER_LIST;
    private final String instigator;
    
    public static HandlerList getHandlerList() {
        return BanWaveEvent.HANDLER_LIST;
    }
    
    public HandlerList getHandlers() {
        return BanWaveEvent.HANDLER_LIST;
    }
    
    @ConstructorProperties({ "instigator" })
    public BanWaveEvent(final String instigator) {
        this.instigator = instigator;
    }
    
    public String getInstigator() {
        return this.instigator;
    }
    
    static {
        HANDLER_LIST = new HandlerList();
    }
}
