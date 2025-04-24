// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.util.ttl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.Map;

public class TtlHashMap<K, V> implements Map<K, V>
{
    private final HashMap<K, V> store;
    private final HashMap<K, Long> timestamps;
    private final long ttl;
    
    public TtlHashMap(final TimeUnit ttlUnit, final long ttlValue) {
        this.store = new HashMap<K, V>();
        this.timestamps = new HashMap<K, Long>();
        this.ttl = ttlUnit.toNanos(ttlValue);
    }
    
    @Override
    public V get(final Object key) {
        final V value = this.store.get(key);
        if (value != null && this.expired(key, value)) {
            this.store.remove(key);
            this.timestamps.remove(key);
            return null;
        }
        return value;
    }
    
    private boolean expired(final Object key, final V value) {
        return System.nanoTime() - this.timestamps.get(key) > this.ttl;
    }
    
    @Override
    public V put(final K key, final V value) {
        this.timestamps.put(key, System.nanoTime());
        return this.store.put(key, value);
    }
    
    @Override
    public int size() {
        return this.store.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.store.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final V value = this.store.get(key);
        if (value != null && this.expired(key, value)) {
            this.store.remove(key);
            this.timestamps.remove(key);
            return false;
        }
        return this.store.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.store.containsValue(value);
    }
    
    @Override
    public V remove(final Object key) {
        this.timestamps.remove(key);
        return this.store.remove(key);
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }
    
    @Override
    public void clear() {
        this.timestamps.clear();
        this.store.clear();
    }
    
    @Override
    public Set<K> keySet() {
        this.clearExpired();
        return Collections.unmodifiableSet((Set<? extends K>)this.store.keySet());
    }
    
    @Override
    public Collection<V> values() {
        this.clearExpired();
        return Collections.unmodifiableCollection((Collection<? extends V>)this.store.values());
    }
    
    @Override
    public Set<Entry<K, V>> entrySet() {
        this.clearExpired();
        return Collections.unmodifiableSet((Set<? extends Entry<K, V>>)this.store.entrySet());
    }
    
    private void clearExpired() {
        for (final K k : this.store.keySet()) {
            this.get(k);
        }
    }
}
