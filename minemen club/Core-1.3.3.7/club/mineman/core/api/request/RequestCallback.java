// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.request;

import org.json.simple.JSONObject;

public interface RequestCallback
{
    void callback(final JSONObject p0);
    
    void error(final String p0);
}
