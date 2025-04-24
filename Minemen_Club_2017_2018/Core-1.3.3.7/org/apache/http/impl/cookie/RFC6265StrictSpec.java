// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.SAFE)
public class RFC6265StrictSpec extends RFC6265CookieSpecBase
{
    static final String[] DATE_PATTERNS;
    
    public RFC6265StrictSpec() {
        super(new CommonCookieAttributeHandler[] { new BasicPathHandler(), new BasicDomainHandler(), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicExpiresHandler(RFC6265StrictSpec.DATE_PATTERNS) });
    }
    
    RFC6265StrictSpec(final CommonCookieAttributeHandler... handlers) {
        super(handlers);
    }
    
    @Override
    public String toString() {
        return "rfc6265-strict";
    }
    
    static {
        DATE_PATTERNS = new String[] { "EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy" };
    }
}
