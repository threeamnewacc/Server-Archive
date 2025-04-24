// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;

@Deprecated
public interface HostNameResolver
{
    InetAddress resolve(final String p0) throws IOException;
}
