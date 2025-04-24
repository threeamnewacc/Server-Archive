// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

public interface HttpConnectionMetrics
{
    long getRequestCount();
    
    long getResponseCount();
    
    long getSentBytesCount();
    
    long getReceivedBytesCount();
    
    Object getMetric(final String p0);
    
    void reset();
}
