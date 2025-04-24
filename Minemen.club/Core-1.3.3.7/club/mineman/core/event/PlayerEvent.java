// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.event;

import java.util.UUID;
import org.bukkit.entity.Player;

public class PlayerEvent extends BaseEvent
{
    private Player player;
    
    public PlayerEvent(final Player player) {
        this.player = player;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }
}
