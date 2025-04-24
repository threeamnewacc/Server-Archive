// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.protocol.HttpContext;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CookieSpecProvider;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpecProvider implements CookieSpecProvider
{
    private volatile CookieSpec cookieSpec;
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new IgnoreSpec();
                }
            }
        }
        return this.cookieSpec;
    }
}
