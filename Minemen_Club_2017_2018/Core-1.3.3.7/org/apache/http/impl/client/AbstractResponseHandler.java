// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.client.ResponseHandler;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public abstract class AbstractResponseHandler<T> implements ResponseHandler<T>
{
    @Override
    public T handleResponse(final HttpResponse response) throws HttpResponseException, IOException {
        final StatusLine statusLine = response.getStatusLine();
        final HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
        return (entity == null) ? null : this.handleEntity(entity);
    }
    
    public abstract T handleEntity(final HttpEntity p0) throws IOException;
}
