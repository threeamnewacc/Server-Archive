// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client;

public class HttpResponseException extends ClientProtocolException
{
    private static final long serialVersionUID = -7186627969477257933L;
    private final int statusCode;
    
    public HttpResponseException(final int statusCode, final String s) {
        super(s);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
}
