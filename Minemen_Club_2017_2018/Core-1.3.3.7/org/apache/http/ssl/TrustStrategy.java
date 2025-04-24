// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.ssl;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public interface TrustStrategy
{
    boolean isTrusted(final X509Certificate[] p0, final String p1) throws CertificateException;
}
