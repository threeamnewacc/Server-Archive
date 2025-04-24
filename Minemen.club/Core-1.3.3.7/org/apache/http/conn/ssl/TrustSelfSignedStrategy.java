// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustSelfSignedStrategy implements TrustStrategy
{
    public static final TrustSelfSignedStrategy INSTANCE;
    
    @Override
    public boolean isTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        return chain.length == 1;
    }
    
    static {
        INSTANCE = new TrustSelfSignedStrategy();
    }
}
