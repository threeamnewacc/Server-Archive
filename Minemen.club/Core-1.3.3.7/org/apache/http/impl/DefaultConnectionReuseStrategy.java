// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl;

import org.apache.http.HeaderIterator;
import org.apache.http.Header;
import org.apache.http.TokenIterator;
import org.apache.http.ProtocolVersion;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.message.BasicTokenIterator;
import org.apache.http.HttpRequest;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.ConnectionReuseStrategy;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class DefaultConnectionReuseStrategy implements ConnectionReuseStrategy
{
    public static final DefaultConnectionReuseStrategy INSTANCE;
    
    @Override
    public boolean keepAlive(final HttpResponse response, final HttpContext context) {
        Args.notNull(response, "HTTP response");
        Args.notNull(context, "HTTP context");
        final HttpRequest request = (HttpRequest)context.getAttribute("http.request");
        if (request != null) {
            try {
                final TokenIterator ti = new BasicTokenIterator(request.headerIterator("Connection"));
                while (ti.hasNext()) {
                    final String token = ti.nextToken();
                    if ("Close".equalsIgnoreCase(token)) {
                        return false;
                    }
                }
            }
            catch (final ParseException px) {
                return false;
            }
        }
        final ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
        final Header teh = response.getFirstHeader("Transfer-Encoding");
        if (teh != null) {
            if (!"chunked".equalsIgnoreCase(teh.getValue())) {
                return false;
            }
        }
        else if (this.canResponseHaveBody(request, response)) {
            final Header[] clhs = response.getHeaders("Content-Length");
            if (clhs.length != 1) {
                return false;
            }
            final Header clh = clhs[0];
            try {
                final int contentLen = Integer.parseInt(clh.getValue());
                if (contentLen < 0) {
                    return false;
                }
            }
            catch (final NumberFormatException ex) {
                return false;
            }
        }
        HeaderIterator headerIterator = response.headerIterator("Connection");
        if (!headerIterator.hasNext()) {
            headerIterator = response.headerIterator("Proxy-Connection");
        }
        if (headerIterator.hasNext()) {
            try {
                final TokenIterator ti2 = new BasicTokenIterator(headerIterator);
                boolean keepalive = false;
                while (ti2.hasNext()) {
                    final String token2 = ti2.nextToken();
                    if ("Close".equalsIgnoreCase(token2)) {
                        return false;
                    }
                    if (!"Keep-Alive".equalsIgnoreCase(token2)) {
                        continue;
                    }
                    keepalive = true;
                }
                if (keepalive) {
                    return true;
                }
            }
            catch (final ParseException px2) {
                return false;
            }
        }
        return !ver.lessEquals(HttpVersion.HTTP_1_0);
    }
    
    protected TokenIterator createTokenIterator(final HeaderIterator hit) {
        return new BasicTokenIterator(hit);
    }
    
    private boolean canResponseHaveBody(final HttpRequest request, final HttpResponse response) {
        if (request != null && request.getRequestLine().getMethod().equalsIgnoreCase("HEAD")) {
            return false;
        }
        final int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status != 204 && status != 304 && status != 205;
    }
    
    static {
        INSTANCE = new DefaultConnectionReuseStrategy();
    }
}
