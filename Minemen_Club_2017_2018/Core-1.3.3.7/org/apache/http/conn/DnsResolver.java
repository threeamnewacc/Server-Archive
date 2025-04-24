// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn;

import java.net.UnknownHostException;
import java.net.InetAddress;

public interface DnsResolver
{
    InetAddress[] resolve(final String p0) throws UnknownHostException;
}
