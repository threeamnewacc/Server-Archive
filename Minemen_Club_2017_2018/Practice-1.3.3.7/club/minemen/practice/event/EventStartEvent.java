// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.event;

import java.beans.ConstructorProperties;
import club.minemen.practice.events.PracticeEvent;
import club.minemen.core.event.BaseEvent;

public class EventStartEvent extends BaseEvent
{
    private final PracticeEvent event;
    
    public PracticeEvent getEvent() {
        return this.event;
    }
    
    @ConstructorProperties({ "event" })
    public EventStartEvent(final PracticeEvent event) {
        this.event = event;
    }
}
