// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.request;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import club.mineman.core.api.APIMessage;

public abstract class AGCRequest implements APIMessage
{
    private final String subChannel;
    
    public String getChannel() {
        return "AGC";
    }
    
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"sub-channel", (Object)this.subChannel, (Object)"agc-key", (Object)"#EH3geeftrlfroT4J9yvgD)k#*h#ndxJ!8f");
    }
    
    @ConstructorProperties({ "subChannel" })
    public AGCRequest(final String subChannel) {
        this.subChannel = subChannel;
    }
}
