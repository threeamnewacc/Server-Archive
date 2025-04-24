// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.events;

import java.beans.ConstructorProperties;
import java.util.UUID;

public class EventPlayer
{
    private final UUID uuid;
    private final PracticeEvent event;
    
    public UUID getUuid() {
        return this.uuid;
    }
    
    public PracticeEvent getEvent() {
        return this.event;
    }
    
    @ConstructorProperties({ "uuid", "event" })
    public EventPlayer(final UUID uuid, final PracticeEvent event) {
        this.uuid = uuid;
        this.event = event;
    }
}
