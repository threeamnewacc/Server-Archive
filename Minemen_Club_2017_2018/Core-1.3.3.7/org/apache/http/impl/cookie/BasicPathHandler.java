// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.util.TextUtils;
import org.apache.http.util.Args;
import org.apache.http.cookie.SetCookie;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicPathHandler implements CommonCookieAttributeHandler
{
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        cookie.setPath(TextUtils.isBlank(value) ? "/" : value);
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
    }
    
    static boolean pathMatch(final String uriPath, final String cookiePath) {
        String normalizedCookiePath = cookiePath;
        if (normalizedCookiePath == null) {
            normalizedCookiePath = "/";
        }
        if (normalizedCookiePath.length() > 1 && normalizedCookiePath.endsWith("/")) {
            normalizedCookiePath = normalizedCookiePath.substring(0, normalizedCookiePath.length() - 1);
        }
        if (uriPath.startsWith(normalizedCookiePath)) {
            if (normalizedCookiePath.equals("/")) {
                return true;
            }
            if (uriPath.length() == normalizedCookiePath.length()) {
                return true;
            }
            if (uriPath.charAt(normalizedCookiePath.length()) == '/') {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        return pathMatch(origin.getPath(), cookie.getPath());
    }
    
    @Override
    public String getAttributeName() {
        return "path";
    }
}
