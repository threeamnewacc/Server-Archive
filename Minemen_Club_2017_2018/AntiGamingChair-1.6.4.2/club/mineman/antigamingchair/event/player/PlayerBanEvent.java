// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.event.player;

import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class PlayerBanEvent extends Event implements Cancellable
{
    private static final HandlerList HANDLER_LIST;
    private final Player player;
    private final String reason;
    private boolean cancelled;
    
    public static HandlerList getHandlerList() {
        return PlayerBanEvent.HANDLER_LIST;
    }
    
    public HandlerList getHandlers() {
        return PlayerBanEvent.HANDLER_LIST;
    }
    
    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @ConstructorProperties({ "player", "reason" })
    public PlayerBanEvent(final Player player, final String reason) {
        this.player = player;
        this.reason = reason;
    }
    
    static {
        HANDLER_LIST = new HandlerList();
    }
}
