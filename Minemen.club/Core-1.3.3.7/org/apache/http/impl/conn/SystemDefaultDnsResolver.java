// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.conn;

import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.http.conn.DnsResolver;

public class SystemDefaultDnsResolver implements DnsResolver
{
    public static final SystemDefaultDnsResolver INSTANCE;
    
    @Override
    public InetAddress[] resolve(final String host) throws UnknownHostException {
        return InetAddress.getAllByName(host);
    }
    
    static {
        INSTANCE = new SystemDefaultDnsResolver();
    }
}
