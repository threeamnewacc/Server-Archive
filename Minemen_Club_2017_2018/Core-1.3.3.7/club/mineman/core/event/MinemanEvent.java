// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.event;

import java.beans.ConstructorProperties;
import java.util.UUID;
import club.mineman.core.mineman.Mineman;

public class MinemanEvent extends BaseEvent
{
    private final Mineman mineman;
    
    public UUID getUniqueId() {
        return this.mineman.getUuid();
    }
    
    public Mineman getMineman() {
        return this.mineman;
    }
    
    @ConstructorProperties({ "mineman" })
    public MinemanEvent(final Mineman mineman) {
        this.mineman = mineman;
    }
}
