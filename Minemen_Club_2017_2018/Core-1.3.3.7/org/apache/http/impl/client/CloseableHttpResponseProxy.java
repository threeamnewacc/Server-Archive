// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.impl.client;

import java.lang.reflect.Proxy;
import org.apache.http.client.methods.CloseableHttpResponse;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.IOException;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.HttpResponse;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;

@Deprecated
class CloseableHttpResponseProxy implements InvocationHandler
{
    private static final Constructor<?> CONSTRUCTOR;
    private final HttpResponse original;
    
    CloseableHttpResponseProxy(final HttpResponse original) {
        this.original = original;
    }
    
    public void close() throws IOException {
        final HttpEntity entity = this.original.getEntity();
        EntityUtils.consume(entity);
    }
    
    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final String mname = method.getName();
        if (mname.equals("close")) {
            this.close();
            return null;
        }
        try {
            return method.invoke(this.original, args);
        }
        catch (final InvocationTargetException ex) {
            final Throwable cause = ex.getCause();
            if (cause != null) {
                throw cause;
            }
            throw ex;
        }
    }
    
    public static CloseableHttpResponse newProxy(final HttpResponse original) {
        try {
            return (CloseableHttpResponse)CloseableHttpResponseProxy.CONSTRUCTOR.newInstance(new CloseableHttpResponseProxy(original));
        }
        catch (final InstantiationException ex) {
            throw new IllegalStateException(ex);
        }
        catch (final InvocationTargetException ex2) {
            throw new IllegalStateException(ex2);
        }
        catch (final IllegalAccessException ex3) {
            throw new IllegalStateException(ex3);
        }
    }
    
    static {
        try {
            CONSTRUCTOR = Proxy.getProxyClass(CloseableHttpResponseProxy.class.getClassLoader(), CloseableHttpResponse.class).getConstructor(InvocationHandler.class);
        }
        catch (final NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
