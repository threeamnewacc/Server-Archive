// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.client.utils;

import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Deprecated
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class Punycode
{
    private static final Idn impl;
    
    public static String toUnicode(final String punycode) {
        return Punycode.impl.toUnicode(punycode);
    }
    
    static {
        Idn _impl;
        try {
            _impl = new JdkIdn();
        }
        catch (final Exception e) {
            _impl = new Rfc3492Idn();
        }
        impl = _impl;
    }
}
