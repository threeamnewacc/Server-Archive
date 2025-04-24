// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client;

import org.apache.http.ProtocolException;
import java.net.URI;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;

@Deprecated
public interface RedirectHandler
{
    boolean isRedirectRequested(final HttpResponse p0, final HttpContext p1);
    
    URI getLocationURI(final HttpResponse p0, final HttpContext p1) throws ProtocolException;
}
