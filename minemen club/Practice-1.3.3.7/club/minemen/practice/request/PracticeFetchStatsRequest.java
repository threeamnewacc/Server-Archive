// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.request;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.UUID;
import club.minemen.core.api.request.Request;

public class PracticeFetchStatsRequest implements Request
{
    private final UUID playerUuid;
    
    public String getPath() {
        return "/practice/" + this.playerUuid.toString();
    }
    
    public Map<String, Object> toMap() {
        return null;
    }
    
    @ConstructorProperties({ "playerUuid" })
    public PracticeFetchStatsRequest(final UUID playerUuid) {
        this.playerUuid = playerUuid;
    }
}
