// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.request.banwave;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import club.mineman.antigamingchair.request.AGCRequest;

public class AGCAddBanWaveRequest extends AGCRequest
{
    private final int id;
    private final String reason;
    
    public AGCAddBanWaveRequest(final int id, final String reason) {
        super("add-ban-wave");
        this.id = id;
        this.reason = reason;
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)new ImmutableMap.Builder().put((Object)"id", (Object)this.id).put((Object)"reason", (Object)this.reason).putAll((Map)super.toMap()).build();
    }
}
