// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn.ssl;

import javax.net.ssl.SSLSession;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import javax.net.ssl.HostnameVerifier;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoopHostnameVerifier implements HostnameVerifier
{
    public static final NoopHostnameVerifier INSTANCE;
    
    @Override
    public boolean verify(final String s, final SSLSession sslSession) {
        return true;
    }
    
    @Override
    public final String toString() {
        return "NO_OP";
    }
    
    static {
        INSTANCE = new NoopHostnameVerifier();
    }
}
