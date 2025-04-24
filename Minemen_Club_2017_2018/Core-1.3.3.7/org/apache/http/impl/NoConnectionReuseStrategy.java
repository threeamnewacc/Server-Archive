// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl;

import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.ConnectionReuseStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NoConnectionReuseStrategy implements ConnectionReuseStrategy
{
    public static final NoConnectionReuseStrategy INSTANCE;
    
    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        return false;
    }
    
    static {
        INSTANCE = new NoConnectionReuseStrategy();
    }
}
