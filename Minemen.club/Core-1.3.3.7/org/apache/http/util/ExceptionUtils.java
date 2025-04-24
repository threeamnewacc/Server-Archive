// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.util;

import java.lang.reflect.Method;

@Deprecated
public final class ExceptionUtils
{
    private static final Method INIT_CAUSE_METHOD;
    
    private static Method getInitCauseMethod() {
        try {
            final Class<?>[] paramsClasses = { Throwable.class };
            return Throwable.class.getMethod("initCause", paramsClasses);
        }
        catch (final NoSuchMethodException e) {
            return null;
        }
    }
    
    public static void initCause(final Throwable throwable, final Throwable cause) {
        if (ExceptionUtils.INIT_CAUSE_METHOD != null) {
            try {
                ExceptionUtils.INIT_CAUSE_METHOD.invoke(throwable, cause);
            }
            catch (final Exception ex) {}
        }
    }
    
    private ExceptionUtils() {
    }
    
    static {
        INIT_CAUSE_METHOD = getInitCauseMethod();
    }
}
