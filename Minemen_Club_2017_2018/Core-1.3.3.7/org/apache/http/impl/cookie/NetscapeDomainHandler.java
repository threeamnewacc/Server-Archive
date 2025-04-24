// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.http.cookie.CookieRestrictionViolationException;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.util.TextUtils;
import org.apache.http.util.Args;
import org.apache.http.cookie.SetCookie;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class NetscapeDomainHandler extends BasicDomainHandler
{
    @Override
    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {
        Args.notNull(cookie, "Cookie");
        if (TextUtils.isBlank(value)) {
            throw new MalformedCookieException("Blank or null value for domain attribute");
        }
        cookie.setDomain(value);
    }
    
    @Override
    public void validate(final Cookie cookie, final CookieOrigin origin) throws MalformedCookieException {
        final String host = origin.getHost();
        final String domain = cookie.getDomain();
        if (!host.equals(domain) && !BasicDomainHandler.domainMatch(domain, host)) {
            throw new CookieRestrictionViolationException("Illegal domain attribute \"" + domain + "\". Domain of origin: \"" + host + "\"");
        }
        if (host.contains(".")) {
            final int domainParts = new StringTokenizer(domain, ".").countTokens();
            if (isSpecialDomain(domain)) {
                if (domainParts < 2) {
                    throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification for " + "special domains");
                }
            }
            else if (domainParts < 3) {
                throw new CookieRestrictionViolationException("Domain attribute \"" + domain + "\" violates the Netscape cookie specification");
            }
        }
    }
    
    private static boolean isSpecialDomain(final String domain) {
        final String ucDomain = domain.toUpperCase(Locale.ROOT);
        return ucDomain.endsWith(".COM") || ucDomain.endsWith(".EDU") || ucDomain.endsWith(".NET") || ucDomain.endsWith(".GOV") || ucDomain.endsWith(".MIL") || ucDomain.endsWith(".ORG") || ucDomain.endsWith(".INT");
    }
    
    @Override
    public boolean match(final Cookie cookie, final CookieOrigin origin) {
        Args.notNull(cookie, "Cookie");
        Args.notNull(origin, "Cookie origin");
        final String host = origin.getHost();
        final String domain = cookie.getDomain();
        return domain != null && host.endsWith(domain);
    }
    
    @Override
    public String getAttributeName() {
        return "domain";
    }
}
