// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.Cookie;
import java.util.Collection;
import org.apache.http.conn.util.PublicSuffixList;
import org.apache.http.util.Args;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.cookie.CommonCookieAttributeHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class PublicSuffixDomainFilter implements CommonCookieAttributeHandler
{
    private final CommonCookieAttributeHandler handler;
    private final PublicSuffixMatcher publicSuffixMatcher;
    private final Map<String, Boolean> localDomainMap;
    
    private static Map<String, Boolean> createLocalDomainMap() {
        final ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<String, Boolean>();
        map.put(".localhost.", Boolean.TRUE);
        map.put(".test.", Boolean.TRUE);
        map.put(".local.", Boolean.TRUE);
        map.put(".local", Boolean.TRUE);
        map.put(".localdomain", Boolean.TRUE);
        return map;
    }
    
    public PublicSuffixDomainFilter(final CommonCookieAttributeHandler handler, final PublicSuffixMatcher publicSuffixMatcher) {
        this.handler = Args.notNull(handler, "Cookie handler");
        this.publicSuffixMatcher = Args.notNull(publicSuffixMatcher, "Public suffix matcher");
        this.localDomainMap = createLocalDomainMap();
    }
    
    public PublicSuffixDomainFilter(final CommonCookieAttributeHandler handler, final PublicSuffixList suffixList) {
        Args.notNull(handler, "Cookie handler");
        Args.notNull(suffixList, "Public suffix list");
        this.handler = handler;
        this.publicSuffixMatcher = new PublicSuffixMatcher(suffixList.getRules(), suffixList.getExceptions());
        this.localDomainMap = createLocalDomainMap();
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        final String host = cookie.getDomain();
        final int i = host.indexOf(46);
        if (i >= 0) {
            final String domain = host.substring(i);
            if (!this.localDomainMap.containsKey(domain) && this.publicSuffixMatcher.matches(host)) {
                return false;
            }
        }
        else if (!host.equalsIgnoreCase(origin.getHost()) && this.publicSuffixMatcher.matches(host)) {
            return false;
        }
        return this.handler.match(cookie, origin);
    }
    
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        this.handler.parse(cookie, value);
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        this.handler.validate(cookie, origin);
    }
    
    @Override
    public String getAttributeName() {
        return this.handler.getAttributeName();
    }
    
    public static CommonCookieAttributeHandler decorate(final CommonCookieAttributeHandler handler, final PublicSuffixMatcher publicSuffixMatcher) {
        Args.notNull(handler, "Cookie attribute handler");
        return (publicSuffixMatcher != null) ? new PublicSuffixDomainFilter(handler, publicSuffixMatcher) : handler;
    }
}
