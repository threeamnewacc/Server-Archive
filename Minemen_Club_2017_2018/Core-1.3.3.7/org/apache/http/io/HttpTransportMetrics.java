// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.io;

public interface HttpTransportMetrics
{
    long getBytesTransferred();
    
    void reset();
}
