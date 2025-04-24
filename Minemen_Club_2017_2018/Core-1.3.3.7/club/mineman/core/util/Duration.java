// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util;

import java.beans.ConstructorProperties;

public enum Duration
{
    SECOND(1000L, "s"), 
    MINUTE(60L * Duration.SECOND.duration, "m"), 
    HOUR(60L * Duration.MINUTE.duration, "h"), 
    DAY(24L * Duration.HOUR.duration, "d"), 
    WEEK(7L * Duration.DAY.duration, "w"), 
    MONTH(30L * Duration.DAY.duration, "M"), 
    YEAR(365L * Duration.DAY.duration, "y");
    
    private final long duration;
    private final String name;
    
    public long getDuration() {
        return this.duration;
    }
    
    public String getName() {
        return this.name;
    }
    
    @ConstructorProperties({ "duration", "name" })
    private Duration(final long duration, final String name) {
        this.duration = duration;
        this.name = name;
    }
}
