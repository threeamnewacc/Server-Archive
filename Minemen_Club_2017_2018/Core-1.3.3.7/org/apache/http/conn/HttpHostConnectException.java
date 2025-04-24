// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn;

import java.util.Arrays;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.http.HttpHost;
import java.net.ConnectException;

public class HttpHostConnectException extends ConnectException
{
    private static final long serialVersionUID = -3194482710275220224L;
    private final HttpHost host;
    
    @Deprecated
    public HttpHostConnectException(final HttpHost host, final ConnectException cause) {
        this(cause, host, (InetAddress[])null);
    }
    
    public HttpHostConnectException(final IOException cause, final HttpHost host, final InetAddress... remoteAddresses) {
        super("Connect to " + ((host != null) ? host.toHostString() : "remote host") + ((remoteAddresses != null && remoteAddresses.length > 0) ? (" " + Arrays.asList(remoteAddresses)) : "") + ((cause != null && cause.getMessage() != null) ? (" failed: " + cause.getMessage()) : " refused"));
        this.host = host;
        this.initCause(cause);
    }
    
    public HttpHost getHost() {
        return this.host;
    }
}
