// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.conn.ConnectionKeepAliveStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultConnectionKeepAliveStrategy implements ConnectionKeepAliveStrategy
{
    public static final DefaultConnectionKeepAliveStrategy INSTANCE;
    
    @Override
    public long getKeepAliveDuration(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        final HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
        while (it.hasNext()) {
            final HeaderElement he = it.nextElement();
            final String param = he.getName();
            final String value = he.getValue();
            if (value != null && param.equalsIgnoreCase("timeout")) {
                try {
                    return Long.parseLong(value) * 1000L;
                }
                catch (final NumberFormatException ex) {}
            }
        }
        return -1L;
    }
    
    static {
        INSTANCE = new DefaultConnectionKeepAliveStrategy();
    }
}
