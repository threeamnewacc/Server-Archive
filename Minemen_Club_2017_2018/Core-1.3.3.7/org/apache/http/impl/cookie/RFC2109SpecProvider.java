// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.cookie.CommonCookieAttributeHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.conn.util.PublicSuffixMatcher;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.Obsolete;
import org.apache.http.cookie.CookieSpecProvider;

@Obsolete
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class RFC2109SpecProvider implements CookieSpecProvider
{
    private final PublicSuffixMatcher publicSuffixMatcher;
    private final boolean oneHeader;
    private volatile CookieSpec cookieSpec;
    
    public RFC2109SpecProvider(final PublicSuffixMatcher publicSuffixMatcher, final boolean oneHeader) {
        this.oneHeader = oneHeader;
        this.publicSuffixMatcher = publicSuffixMatcher;
    }
    
    public RFC2109SpecProvider(final PublicSuffixMatcher publicSuffixMatcher) {
        this(publicSuffixMatcher, false);
    }
    
    public RFC2109SpecProvider() {
        this(null, false);
    }
    
    @Override
    public CookieSpec create(final HttpContext context) {
        if (this.cookieSpec == null) {
            synchronized (this) {
                if (this.cookieSpec == null) {
                    this.cookieSpec = new RFC2109Spec(this.oneHeader, new CommonCookieAttributeHandler[] { new RFC2109VersionHandler(), new BasicPathHandler(), PublicSuffixDomainFilter.decorate(new RFC2109DomainHandler(), this.publicSuffixMatcher), new BasicMaxAgeHandler(), new BasicSecureHandler(), new BasicCommentHandler() });
                }
            }
        }
        return this.cookieSpec;
    }
}
