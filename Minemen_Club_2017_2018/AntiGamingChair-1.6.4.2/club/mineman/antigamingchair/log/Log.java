// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.log;

import java.beans.ConstructorProperties;

public class Log
{
    private final long timestamp;
    private final int minemanId;
    private final String log;
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public int getMinemanId() {
        return this.minemanId;
    }
    
    public String getLog() {
        return this.log;
    }
    
    @ConstructorProperties({ "minemanId", "log" })
    public Log(final int minemanId, final String log) {
        this.timestamp = System.currentTimeMillis();
        this.minemanId = minemanId;
        this.log = log;
    }
}
