// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

public interface HttpEntityEnclosingRequest extends HttpRequest
{
    boolean expectContinue();
    
    void setEntity(final HttpEntity p0);
    
    HttpEntity getEntity();
}
