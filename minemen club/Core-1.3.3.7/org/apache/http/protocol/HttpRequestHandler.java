// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

public interface HttpRequestHandler
{
    void handle(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws HttpException, IOException;
}
