// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util;

import java.beans.ConstructorProperties;

public class BanWrapper
{
    private final String message;
    private final boolean banned;
    
    public String getMessage() {
        return this.message;
    }
    
    public boolean isBanned() {
        return this.banned;
    }
    
    @ConstructorProperties({ "message", "banned" })
    public BanWrapper(final String message, final boolean banned) {
        this.message = message;
        this.banned = banned;
    }
}
