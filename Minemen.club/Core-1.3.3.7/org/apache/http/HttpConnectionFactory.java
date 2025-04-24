// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

import java.io.IOException;
import java.net.Socket;

public interface HttpConnectionFactory<T extends HttpConnection>
{
    T createConnection(final Socket p0) throws IOException;
}
