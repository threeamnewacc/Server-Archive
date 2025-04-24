// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.auth;

import org.apache.http.protocol.HttpContext;

public interface AuthSchemeProvider
{
    AuthScheme create(final HttpContext p0);
}
