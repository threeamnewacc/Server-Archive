// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;

public interface HttpResponseInterceptor
{
    void process(final HttpResponse p0, final HttpContext p1) throws HttpException, IOException;
}
