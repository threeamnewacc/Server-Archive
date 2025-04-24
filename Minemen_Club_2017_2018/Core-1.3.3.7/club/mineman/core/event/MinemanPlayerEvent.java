// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.event;

import java.util.UUID;
import club.mineman.core.mineman.Mineman;

public class MinemanPlayerEvent extends PlayerEvent
{
    private final Mineman mineman;
    
    public MinemanPlayerEvent(final Mineman mineman) {
        super(mineman.getPlayer());
        this.mineman = mineman;
    }
    
    @Override
    public UUID getUniqueId() {
        return this.mineman.getUuid();
    }
    
    public Mineman getMineman() {
        return this.mineman;
    }
}
