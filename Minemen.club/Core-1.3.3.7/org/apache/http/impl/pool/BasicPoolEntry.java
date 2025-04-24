// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.pool;

import java.io.IOException;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpHost;
import org.apache.http.pool.PoolEntry;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicPoolEntry extends PoolEntry<HttpHost, HttpClientConnection>
{
    public BasicPoolEntry(final String id, final HttpHost route, final HttpClientConnection conn) {
        super(id, route, conn);
    }
    
    @Override
    public void close() {
        try {
            ((PoolEntry<T, HttpClientConnection>)this).getConnection().close();
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public boolean isClosed() {
        return !((PoolEntry<T, HttpClientConnection>)this).getConnection().isOpen();
    }
}
