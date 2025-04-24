// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.queue;

import java.beans.ConstructorProperties;

public enum QueueType
{
    UNRANKED("Unranked"), 
    RANKED("Ranked"), 
    PREMIUM("Premium");
    
    private final String name;
    
    public boolean isRanked() {
        return this != QueueType.UNRANKED;
    }
    
    public String getName() {
        return this.name;
    }
    
    @ConstructorProperties({ "name" })
    private QueueType(final String name) {
        this.name = name;
    }
}
