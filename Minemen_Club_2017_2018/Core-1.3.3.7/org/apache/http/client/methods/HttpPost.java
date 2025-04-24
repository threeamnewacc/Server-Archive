// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.methods;

import java.net.URI;

public class HttpPost extends HttpEntityEnclosingRequestBase
{
    public static final String METHOD_NAME = "POST";
    
    public HttpPost() {
    }
    
    public HttpPost(final URI uri) {
        this.setURI(uri);
    }
    
    public HttpPost(final String uri) {
        this.setURI(URI.create(uri));
    }
    
    @Override
    public String getMethod() {
        return "POST";
    }
}
