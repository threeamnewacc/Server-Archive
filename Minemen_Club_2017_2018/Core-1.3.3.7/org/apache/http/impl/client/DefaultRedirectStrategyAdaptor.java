// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import java.net.URI;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.ProtocolException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.client.RedirectHandler;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.RedirectStrategy;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
class DefaultRedirectStrategyAdaptor implements RedirectStrategy
{
    private final RedirectHandler handler;
    
    public DefaultRedirectStrategyAdaptor(final RedirectHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public boolean isRedirected(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        return this.handler.isRedirectRequested(response, context);
    }
    
    @Override
    public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response, final HttpContext context) throws ProtocolException {
        final URI uri = this.handler.getLocationURI(response, context);
        final String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase("HEAD")) {
            return new HttpHead(uri);
        }
        return new HttpGet(uri);
    }
    
    public RedirectHandler getHandler() {
        return this.handler;
    }
}
