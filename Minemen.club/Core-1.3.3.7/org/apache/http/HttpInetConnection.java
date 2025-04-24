// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

import java.net.InetAddress;

public interface HttpInetConnection extends HttpConnection
{
    InetAddress getLocalAddress();
    
    int getLocalPort();
    
    InetAddress getRemoteAddress();
    
    int getRemotePort();
}
