// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.manager;

import java.util.concurrent.ConcurrentLinkedQueue;
import club.mineman.antigamingchair.log.Log;
import java.util.Queue;

public class LogManager
{
    private final Queue<Log> logQueue;
    
    public LogManager() {
        this.logQueue = new ConcurrentLinkedQueue<Log>();
    }
    
    public void addToLogQueue(final Log log) {
        this.logQueue.add(log);
    }
    
    public void clearLogQueue() {
        this.logQueue.clear();
    }
    
    public Queue<Log> getLogQueue() {
        return this.logQueue;
    }
}
