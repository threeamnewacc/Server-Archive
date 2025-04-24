// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.methods;

import org.apache.http.concurrent.Cancellable;

public interface HttpExecutionAware
{
    boolean isAborted();
    
    void setCancellable(final Cancellable p0);
}
