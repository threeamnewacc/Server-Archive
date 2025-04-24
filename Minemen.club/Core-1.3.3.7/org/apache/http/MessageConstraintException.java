// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

import java.nio.charset.CharacterCodingException;

public class MessageConstraintException extends CharacterCodingException
{
    private static final long serialVersionUID = 6077207720446368695L;
    private final String message;
    
    public MessageConstraintException(final String message) {
        this.message = message;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
}
