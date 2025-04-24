// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.event.bungee;

import org.bukkit.entity.Player;
import club.mineman.core.event.PlayerEvent;

public class BungeeReceivedEvent extends PlayerEvent
{
    private final String channel;
    private final String message;
    private final byte[] messageBytes;
    private final boolean isValid;
    
    public BungeeReceivedEvent(final Player player, final String channel, final String message, final byte[] messageBytes, final boolean isValid) {
        super(player);
        this.channel = channel;
        this.message = message;
        this.messageBytes = messageBytes;
        this.isValid = isValid;
    }
    
    public String getChannel() {
        return this.channel;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public byte[] getMessageBytes() {
        return this.messageBytes;
    }
    
    public boolean isValid() {
        return this.isValid;
    }
}
