// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.protocol;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;

public interface HttpExpectationVerifier
{
    void verify(final HttpRequest p0, final HttpResponse p1, final HttpContext p2) throws HttpException;
}
