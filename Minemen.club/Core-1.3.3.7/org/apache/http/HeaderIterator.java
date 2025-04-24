// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http;

import java.util.Iterator;

public interface HeaderIterator extends Iterator<Object>
{
    boolean hasNext();
    
    Header nextHeader();
}
