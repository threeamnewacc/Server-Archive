// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class JdkIdn implements Idn
{
    private final Method toUnicode;
    
    public JdkIdn() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("java.net.IDN");
        try {
            this.toUnicode = clazz.getMethod("toUnicode", String.class);
        }
        catch (final SecurityException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        catch (final NoSuchMethodException e2) {
            throw new IllegalStateException(e2.getMessage(), e2);
        }
    }
    
    @Override
    public String toUnicode(final String punycode) {
        try {
            return (String)this.toUnicode.invoke(null, punycode);
        }
        catch (final IllegalAccessException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
        catch (final InvocationTargetException e2) {
            final Throwable t = e2.getCause();
            throw new RuntimeException(t.getMessage(), t);
        }
    }
}
