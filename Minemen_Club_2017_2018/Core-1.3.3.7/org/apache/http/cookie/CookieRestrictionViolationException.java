// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.cookie;

public class CookieRestrictionViolationException extends MalformedCookieException
{
    private static final long serialVersionUID = 7371235577078589013L;
    
    public CookieRestrictionViolationException() {
    }
    
    public CookieRestrictionViolationException(final String message) {
        super(message);
    }
}
