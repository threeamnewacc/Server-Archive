// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import org.apache.http.protocol.HttpContext;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.UserTokenHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoopUserTokenHandler implements UserTokenHandler
{
    public static final NoopUserTokenHandler INSTANCE;
    
    @Override
    public Object getUserToken(final HttpContext context) {
        return null;
    }
    
    static {
        INSTANCE = new NoopUserTokenHandler();
    }
}
