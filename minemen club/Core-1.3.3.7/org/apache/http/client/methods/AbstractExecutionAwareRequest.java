// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.methods;

import org.apache.http.params.HttpParams;
import org.apache.http.client.utils.CloneUtils;
import org.apache.http.message.HeaderGroup;
import java.io.IOException;
import org.apache.http.conn.ConnectionReleaseTrigger;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.concurrent.Cancellable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.http.HttpRequest;
import org.apache.http.message.AbstractHttpMessage;

public abstract class AbstractExecutionAwareRequest extends AbstractHttpMessage implements HttpExecutionAware, AbortableHttpRequest, Cloneable, HttpRequest
{
    private final AtomicBoolean aborted;
    private final AtomicReference<Cancellable> cancellableRef;
    
    protected AbstractExecutionAwareRequest() {
        this.aborted = new AtomicBoolean(false);
        this.cancellableRef = new AtomicReference<Cancellable>(null);
    }
    
    @Deprecated
    @Override
    public void setConnectionRequest(final ClientConnectionRequest connRequest) {
        this.setCancellable(new Cancellable() {
            @Override
            public boolean cancel() {
                connRequest.abortRequest();
                return true;
            }
        });
    }
    
    @Deprecated
    @Override
    public void setReleaseTrigger(final ConnectionReleaseTrigger releaseTrigger) {
        this.setCancellable(new Cancellable() {
            @Override
            public boolean cancel() {
                try {
                    releaseTrigger.abortConnection();
                    return true;
                }
                catch (final IOException ex) {
                    return false;
                }
            }
        });
    }
    
    @Override
    public void abort() {
        if (this.aborted.compareAndSet(false, true)) {
            final Cancellable cancellable = this.cancellableRef.getAndSet(null);
            if (cancellable != null) {
                cancellable.cancel();
            }
        }
    }
    
    @Override
    public boolean isAborted() {
        return this.aborted.get();
    }
    
    @Override
    public void setCancellable(final Cancellable cancellable) {
        if (!this.aborted.get()) {
            this.cancellableRef.set(cancellable);
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        final AbstractExecutionAwareRequest clone = (AbstractExecutionAwareRequest)super.clone();
        clone.headergroup = CloneUtils.cloneObject(this.headergroup);
        clone.params = CloneUtils.cloneObject(this.params);
        return clone;
    }
    
    public void completed() {
        this.cancellableRef.set(null);
    }
    
    public void reset() {
        final Cancellable cancellable = this.cancellableRef.getAndSet(null);
        if (cancellable != null) {
            cancellable.cancel();
        }
        this.aborted.set(false);
    }
}
