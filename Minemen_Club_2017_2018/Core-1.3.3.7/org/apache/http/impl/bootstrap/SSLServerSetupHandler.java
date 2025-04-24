// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.bootstrap;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLServerSocket;

public interface SSLServerSetupHandler
{
    void initialize(final SSLServerSocket p0) throws SSLException;
}
