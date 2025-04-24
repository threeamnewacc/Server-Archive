// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.event;

import java.beans.ConstructorProperties;
import club.minemen.practice.player.PlayerData;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Event;

public class PlayerDataRetrieveEvent extends Event
{
    private static final HandlerList HANDLERS;
    private final PlayerData playerData;
    
    public static HandlerList getHandlerList() {
        return PlayerDataRetrieveEvent.HANDLERS;
    }
    
    public HandlerList getHandlers() {
        return PlayerDataRetrieveEvent.HANDLERS;
    }
    
    public PlayerData getPlayerData() {
        return this.playerData;
    }
    
    @ConstructorProperties({ "playerData" })
    public PlayerDataRetrieveEvent(final PlayerData playerData) {
        this.playerData = playerData;
    }
    
    static {
        HANDLERS = new HandlerList();
    }
}
