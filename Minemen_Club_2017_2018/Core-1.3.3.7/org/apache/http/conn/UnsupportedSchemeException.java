// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn;

import java.io.IOException;

public class UnsupportedSchemeException extends IOException
{
    private static final long serialVersionUID = 3597127619218687636L;
    
    public UnsupportedSchemeException(final String message) {
        super(message);
    }
}
