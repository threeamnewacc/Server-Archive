// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.MalformedCookieException;
import java.util.Collections;
import org.apache.http.cookie.Cookie;
import java.util.List;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.Header;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class IgnoreSpec extends CookieSpecBase
{
    @Override
    public int getVersion() {
        return 0;
    }
    
    @Override
    public List<Cookie> parse(final Header header, final CookieOrigin origin) throws MalformedCookieException {
        return Collections.emptyList();
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return false;
    }
    
    @Override
    public List<Header> formatCookies(final List<Cookie> cookies) {
        return Collections.emptyList();
    }
    
    @Override
    public Header getVersionHeader() {
        return null;
    }
}
