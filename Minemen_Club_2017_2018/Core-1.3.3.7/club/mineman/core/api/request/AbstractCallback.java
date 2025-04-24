// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.request;

import java.beans.ConstructorProperties;
import club.mineman.core.CorePlugin;

public abstract class AbstractCallback implements RequestCallback
{
    private final String errorMessage;
    
    @Override
    public void error(final String message) {
        CorePlugin.getInstance().getServer().getLogger().warning(this.errorMessage + " " + message);
    }
    
    @ConstructorProperties({ "errorMessage" })
    public AbstractCallback(final String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
