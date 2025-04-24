// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import club.mineman.core.api.APIMessage;

public class IgnoreUpdateRequest implements APIMessage
{
    private final String name;
    private final int id;
    
    @Override
    public String getChannel() {
        return "Update-Ignore";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"name", (Object)this.name, (Object)"id", (Object)this.id);
    }
    
    @ConstructorProperties({ "name", "id" })
    public IgnoreUpdateRequest(final String name, final int id) {
        this.name = name;
        this.id = id;
    }
}
