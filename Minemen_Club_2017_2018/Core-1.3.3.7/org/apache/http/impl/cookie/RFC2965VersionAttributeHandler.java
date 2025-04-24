// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.SetCookie2;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.util.Args;
import org.apache.http.cookie.SetCookie;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RFC2965VersionAttributeHandler implements CommonCookieAttributeHandler
{
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (value == null) {
            throw new MalformedCookieException("Missing value for version attribute");
        }
        int version = -1;
        try {
            version = Integer.parseInt(value);
        }
        catch (final NumberFormatException e) {
            version = -1;
        }
        if (version < 0) {
            throw new MalformedCookieException("Invalid cookie version.");
        }
        cookie.setVersion(version);
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (cookie instanceof SetCookie2 && cookie instanceof ClientCookie && !((ClientCookie)cookie).containsAttribute("version")) {
            throw new CookieRestrictionViolationException("Violates RFC 2965. Version attribute is required.");
        }
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        return true;
    }
    
    @Override
    public String getAttributeName() {
        return "version";
    }
}
