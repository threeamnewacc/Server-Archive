// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.protocol.HttpContext;
import org.apache.http.HttpRequest;
import java.util.List;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpRequestInterceptor;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class RequestAcceptEncoding implements HttpRequestInterceptor
{
    private final String acceptEncoding;
    
    public RequestAcceptEncoding(final List<String> encodings) {
        if (encodings != null && !encodings.isEmpty()) {
            final StringBuilder buf = new StringBuilder();
            for (int i = 0; i < encodings.size(); ++i) {
                if (i > 0) {
                    buf.append(",");
                }
                buf.append(encodings.get(i));
            }
            this.acceptEncoding = buf.toString();
        }
        else {
            this.acceptEncoding = "gzip,deflate";
        }
    }
    
    public RequestAcceptEncoding() {
        this(null);
    }
    
    @Override
    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig requestConfig = clientContext.getRequestConfig();
        if (!request.containsHeader("Accept-Encoding") && requestConfig.isContentCompressionEnabled()) {
            request.addHeader("Accept-Encoding", this.acceptEncoding);
        }
    }
}
