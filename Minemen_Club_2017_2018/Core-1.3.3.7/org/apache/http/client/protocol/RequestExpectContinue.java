// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.HttpVersion;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.util.Args;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestExpectContinue implements HttpRequestInterceptor
{
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        Args.notNull(request, "HTTP request");
        if (!request.containsHeader("Expect") && request instanceof HttpEntityEnclosingRequest) {
            final ProtocolVersion ver = request.getRequestLine().getProtocolVersion();
            final HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            if (entity != null && entity.getContentLength() != 0L && !ver.lessEquals(HttpVersion.HTTP_1_0)) {
                final HttpClientContext clientContext = HttpClientContext.adapt(context);
                final RequestConfig config = clientContext.getRequestConfig();
                if (config.isExpectContinueEnabled()) {
                    request.addHeader("Expect", "100-continue");
                }
            }
        }
    }
}
