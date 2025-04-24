// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.cookie;

import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.SAFE)
public class BestMatchSpec extends DefaultCookieSpec
{
    public BestMatchSpec(final String[] datepatterns, final boolean oneHeader) {
        super(datepatterns, oneHeader);
    }
    
    public BestMatchSpec() {
        this(null, false);
    }
    
    @Override
    public String toString() {
        return "best-match";
    }
}
