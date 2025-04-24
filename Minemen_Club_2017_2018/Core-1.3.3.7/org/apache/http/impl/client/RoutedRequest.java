// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import org.apache.http.conn.routing.HttpRoute;

@Deprecated
public class RoutedRequest
{
    protected final RequestWrapper request;
    protected final HttpRoute route;
    
    public RoutedRequest(final RequestWrapper req, final HttpRoute route) {
        this.request = req;
        this.route = route;
    }
    
    public final RequestWrapper getRequest() {
        return this.request;
    }
    
    public final HttpRoute getRoute() {
        return this.route;
    }
}
