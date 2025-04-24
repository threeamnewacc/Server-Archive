// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn;

import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

@Deprecated
public interface ClientConnectionManagerFactory
{
    ClientConnectionManager newInstance(final HttpParams p0, final SchemeRegistry p1);
}
