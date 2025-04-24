// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.config.SocketConfig;
import java.net.InetSocketAddress;
import org.apache.http.HttpHost;

public interface HttpClientConnectionOperator
{
    void connect(final ManagedHttpClientConnection p0, final HttpHost p1, final InetSocketAddress p2, final int p3, final SocketConfig p4, final HttpContext p5) throws IOException;
    
    void upgrade(final ManagedHttpClientConnection p0, final HttpHost p1, final HttpContext p2) throws IOException;
}
