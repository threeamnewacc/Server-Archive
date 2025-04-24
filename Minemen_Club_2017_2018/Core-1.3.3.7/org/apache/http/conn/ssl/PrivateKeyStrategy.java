// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn.ssl;

import java.net.Socket;
import java.util.Map;

@Deprecated
public interface PrivateKeyStrategy
{
    String chooseAlias(final Map<String, PrivateKeyDetails> p0, final Socket p1);
}
