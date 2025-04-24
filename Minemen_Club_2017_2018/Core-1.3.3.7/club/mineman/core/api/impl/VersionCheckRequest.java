// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import club.mineman.core.api.APIMessage;

public class VersionCheckRequest implements APIMessage
{
    private final String pluginName;
    
    @Override
    public String getChannel() {
        return "Version-Check";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"plugin", (Object)this.pluginName);
    }
    
    @ConstructorProperties({ "pluginName" })
    public VersionCheckRequest(final String pluginName) {
        this.pluginName = pluginName;
    }
}
