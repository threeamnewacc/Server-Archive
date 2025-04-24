// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.SAFE)
public class RFC6265LaxSpec extends RFC6265CookieSpecBase
{
    public RFC6265LaxSpec() {
        super(new CommonCookieAttributeHandler[] { new BasicPathHandler(), new BasicDomainHandler(), new LaxMaxAgeHandler(), new BasicSecureHandler(), new LaxExpiresHandler() });
    }
    
    RFC6265LaxSpec(final CommonCookieAttributeHandler... handlers) {
        super(handlers);
    }
    
    @Override
    public String toString() {
        return "rfc6265-lax";
    }
}
