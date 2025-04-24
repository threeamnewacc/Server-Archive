// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.event.match;

import java.beans.ConstructorProperties;
import club.minemen.practice.match.Match;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class MatchEvent extends Event
{
    private static final HandlerList HANDLERS;
    private final Match match;
    
    public static HandlerList getHandlerList() {
        return MatchEvent.HANDLERS;
    }
    
    public HandlerList getHandlers() {
        return MatchEvent.HANDLERS;
    }
    
    public Match getMatch() {
        return this.match;
    }
    
    @ConstructorProperties({ "match" })
    public MatchEvent(final Match match) {
        this.match = match;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
