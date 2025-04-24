// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

public interface RequestLine
{
    String getMethod();
    
    ProtocolVersion getProtocolVersion();
    
    String getUri();
}
