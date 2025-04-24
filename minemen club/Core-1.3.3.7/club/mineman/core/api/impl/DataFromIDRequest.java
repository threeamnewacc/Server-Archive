// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import club.mineman.core.api.APIMessage;

public class DataFromIDRequest implements APIMessage
{
    private final int id;
    
    @Override
    public String getChannel() {
        return "Data-From-Player-ID";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"id", (Object)this.id);
    }
    
    @ConstructorProperties({ "id" })
    public DataFromIDRequest(final int id) {
        this.id = id;
    }
}
