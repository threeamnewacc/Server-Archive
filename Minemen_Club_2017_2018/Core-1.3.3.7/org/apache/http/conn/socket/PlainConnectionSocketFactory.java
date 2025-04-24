// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn.socket;

import java.net.SocketAddress;
import java.net.InetSocketAddress;
import org.apache.http.HttpHost;
import java.io.IOException;
import java.net.Socket;
import org.apache.http.protocol.HttpContext;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class PlainConnectionSocketFactory implements ConnectionSocketFactory
{
    public static final PlainConnectionSocketFactory INSTANCE;
    
    public static PlainConnectionSocketFactory getSocketFactory() {
        return PlainConnectionSocketFactory.INSTANCE;
    }
    
    @Override
    public Socket createSocket(final HttpContext context) throws IOException {
        return new Socket();
    }
    
    @Override
    public Socket connectSocket(final int connectTimeout, final Socket socket, final HttpHost host, final InetSocketAddress remoteAddress, final InetSocketAddress localAddress, final HttpContext context) throws IOException {
        final Socket sock = (socket != null) ? socket : this.createSocket(context);
        if (localAddress != null) {
            sock.bind(localAddress);
        }
        try {
            sock.connect(remoteAddress, connectTimeout);
        }
        catch (final IOException ex) {
            try {
                sock.close();
            }
            catch (final IOException ex2) {}
            throw ex;
        }
        return sock;
    }
    
    static {
        INSTANCE = new PlainConnectionSocketFactory();
    }
}
