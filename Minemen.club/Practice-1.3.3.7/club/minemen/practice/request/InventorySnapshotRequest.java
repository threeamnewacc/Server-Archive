// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.request;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONObject;
import club.minemen.core.api.request.Request;

public class InventorySnapshotRequest implements Request
{
    private final JSONObject inventoryA;
    private final JSONObject inventoryB;
    private final UUID matchId;
    
    public String getPath() {
        return "/matches/insert/inventory";
    }
    
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"match-id", (Object)this.matchId.toString(), (Object)"inventory-a", (Object)this.inventoryA.toJSONString(), (Object)"inventory-b", (Object)this.inventoryB.toJSONString());
    }
    
    @ConstructorProperties({ "inventoryA", "inventoryB", "matchId" })
    public InventorySnapshotRequest(final JSONObject inventoryA, final JSONObject inventoryB, final UUID matchId) {
        this.inventoryA = inventoryA;
        this.inventoryB = inventoryB;
        this.matchId = matchId;
    }
}
