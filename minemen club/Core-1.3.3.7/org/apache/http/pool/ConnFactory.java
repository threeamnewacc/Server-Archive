// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.pool;

import java.io.IOException;

public interface ConnFactory<T, C>
{
    C create(final T p0) throws IOException;
}
