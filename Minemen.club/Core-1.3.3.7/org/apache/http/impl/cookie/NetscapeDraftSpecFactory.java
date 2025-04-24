// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.protocol.HttpContext;
import java.util.Collection;
import org.apache.http.params.HttpParams;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.CookieSpecFactory;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NetscapeDraftSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    private final CookieSpec cookieSpec;
    
    public NetscapeDraftSpecFactory(final String[] datepatterns) {
        this.cookieSpec = new NetscapeDraftSpec(datepatterns);
    }
    
    public NetscapeDraftSpecFactory() {
        this(null);
    }
    
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        if (params != null) {
            String[] patterns = null;
            final Collection<?> param = (Collection<?>)params.getParameter("http.protocol.cookie-datepatterns");
            if (param != null) {
                patterns = new String[param.size()];
                patterns = param.toArray(patterns);
            }
            return new NetscapeDraftSpec(patterns);
        }
        return new NetscapeDraftSpec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return this.cookieSpec;
    }
}
