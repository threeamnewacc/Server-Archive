// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

import java.io.IOException;

public class ConnectionClosedException extends IOException
{
    private static final long serialVersionUID = 617550366255636674L;
    
    public ConnectionClosedException(final String message) {
        super(message);
    }
}
