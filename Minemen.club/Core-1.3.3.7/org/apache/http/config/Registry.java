// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.config;

import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;

@Contract(threading = ThreadingBehavior.SAFE)
public final class Registry<I> implements Lookup<I>
{
    private final Map<String, I> map;
    
    Registry(final Map<String, I> map) {
        this.map = new ConcurrentHashMap<String, I>((Map<? extends String, ? extends I>)map);
    }
    
    @Override
    public I lookup(final String key) {
        if (key == null) {
            return null;
        }
        return this.map.get(key.toLowerCase(Locale.ROOT));
    }
    
    @Override
    public String toString() {
        return this.map.toString();
    }
}
