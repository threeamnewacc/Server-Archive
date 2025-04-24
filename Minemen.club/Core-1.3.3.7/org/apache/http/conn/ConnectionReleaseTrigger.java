// 
// Decompiled by Procyon v0.6.0
// 

package org.apache.http.conn;

import java.io.IOException;

public interface ConnectionReleaseTrigger
{
    void releaseConnection() throws IOException;
    
    void abortConnection() throws IOException;
}
