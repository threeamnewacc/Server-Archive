// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.auth;

public class UnsupportedDigestAlgorithmException extends RuntimeException
{
    private static final long serialVersionUID = 319558534317118022L;
    
    public UnsupportedDigestAlgorithmException() {
    }
    
    public UnsupportedDigestAlgorithmException(final String message) {
        super(message);
    }
    
    public UnsupportedDigestAlgorithmException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
