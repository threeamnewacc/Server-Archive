// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import club.mineman.core.api.APIMessage;

public class PunishHistoryRequest implements APIMessage
{
    private final String name;
    
    @Override
    public String getChannel() {
        return "PunishHistory";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"name", (Object)this.name);
    }
    
    @ConstructorProperties({ "name" })
    public PunishHistoryRequest(final String name) {
        this.name = name;
    }
}
