// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.protocol.HttpContext;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.cookie.CookieSpecProvider;

@Obsolete
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NetscapeDraftSpecProvider implements CookieSpecProvider
{
    private final String[] datepatterns;
    private volatile CookieSpec cookieSpec;
    
    public NetscapeDraftSpecProvider(final String[] datepatterns) {
        this.datepatterns = datepatterns;
    }
    
    public NetscapeDraftSpecProvider() {
        this(null);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new NetscapeDraftSpec(this.datepatterns);
                }
            }
        }
        return this.cookieSpec;
    }
}
