// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.config;

import java.util.Locale;
import org.apache.http.util.Args;
import java.util.HashMap;
import java.util.Map;

public final class RegistryBuilder<I>
{
    private final Map<String, I> items;
    
    public static <I> RegistryBuilder<I> create() {
        return new RegistryBuilder<I>();
    }
    
    RegistryBuilder() {
        this.items = new HashMap<String, I>();
    }
    
    public RegistryBuilder<I> register(final String id, final I item) {
        Args.notEmpty(id, "ID");
        Args.notNull(item, "Item");
        this.items.put(id.toLowerCase(Locale.ROOT), item);
        return this;
    }
    
    public Registry<I> build() {
        return new Registry<I>(this.items);
    }
    
    @Override
    public String toString() {
        return this.items.toString();
    }
}
