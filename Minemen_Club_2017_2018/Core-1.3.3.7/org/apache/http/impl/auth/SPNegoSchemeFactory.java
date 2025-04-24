// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.auth;

import org.apache.http.protocol.HttpContext;
import org.apache.http.auth.AuthScheme;
import org.apache.http.params.HttpParams;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthSchemeFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class SPNegoSchemeFactory implements AuthSchemeFactory, AuthSchemeProvider
{
    private final boolean stripPort;
    private final boolean useCanonicalHostname;
    
    public SPNegoSchemeFactory(final boolean stripPort, final boolean useCanonicalHostname) {
        this.stripPort = stripPort;
        this.useCanonicalHostname = useCanonicalHostname;
    }
    
    public SPNegoSchemeFactory(final boolean stripPort) {
        this.stripPort = stripPort;
        this.useCanonicalHostname = true;
    }
    
    public SPNegoSchemeFactory() {
        this(true, true);
    }
    
    public boolean isStripPort() {
        return this.stripPort;
    }
    
    public boolean isUseCanonicalHostname() {
        return this.useCanonicalHostname;
    }
    
    @Override
    public AuthScheme newInstance(final HttpParams params) {
        return new SPNegoScheme(this.stripPort, this.useCanonicalHostname);
    }
    
    @Override
    public AuthScheme create(final HttpContext context) {
        return new SPNegoScheme(this.stripPort, this.useCanonicalHostname);
    }
}
