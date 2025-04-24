// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.io;

import org.apache.http.HttpMessage;

public interface HttpMessageWriterFactory<T extends HttpMessage>
{
    HttpMessageWriter<T> create(final SessionOutputBuffer p0);
}
