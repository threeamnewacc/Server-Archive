// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.protocol.HttpContext;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.cookie.CookieSpecFactory;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpecFactory implements CookieSpecFactory, CookieSpecProvider
{
    @Override
    public CookieSpec newInstance(final HttpParams params) {
        return new IgnoreSpec();
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        return new IgnoreSpec();
    }
}
