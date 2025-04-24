// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.methods;

import org.apache.http.client.utils.CloneUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;

public abstract class HttpEntityEnclosingRequestBase extends HttpRequestBase implements HttpEntityEnclosingRequest
{
    private HttpEntity entity;
    
    @Override
    public HttpEntity getEntity() {
        return this.entity;
    }
    
    @Override
    public void setEntity(final HttpEntity entity) {
        this.entity = entity;
    }
    
    @Override
    public boolean expectContinue() {
        final Header expect = this.getFirstHeader("Expect");
        return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final HttpEntityEnclosingRequestBase clone = (HttpEntityEnclosingRequestBase)super.clone();
        if (this.entity != null) {
            clone.entity = CloneUtils.cloneObject(this.entity);
        }
        return clone;
    }
}
