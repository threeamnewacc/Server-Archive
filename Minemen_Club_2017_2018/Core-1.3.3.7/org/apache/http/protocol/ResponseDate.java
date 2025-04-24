// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.util.Args;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpResponseInterceptor;

@Contract(threading = ThreadingBehavior.SAFE)
public class ResponseDate implements HttpResponseInterceptor
{
    private static final HttpDateGenerator DATE_GENERATOR;
    
    @Override
    public void process(final HttpResponse response, final HttpContext context) throws HttpException, IOException {
        Args.notNull(response, "HTTP response");
        final int status = response.getStatusLine().getStatusCode();
        if (status >= 200 && !response.containsHeader("Date")) {
            final String httpdate = ResponseDate.DATE_GENERATOR.getCurrentDate();
            response.setHeader("Date", httpdate);
        }
    }
    
    static {
        DATE_GENERATOR = new HttpDateGenerator();
    }
}
