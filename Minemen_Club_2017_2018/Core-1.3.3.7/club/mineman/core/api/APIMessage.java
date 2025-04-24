// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api;

import java.util.Map;

public interface APIMessage
{
    String getChannel();
    
    Map<String, Object> toMap();
}
