// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

public interface StatusLine
{
    ProtocolVersion getProtocolVersion();
    
    int getStatusCode();
    
    String getReasonPhrase();
}
