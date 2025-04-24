// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.sql.Timestamp;
import club.mineman.core.api.APIMessage;

public class PunishmentRequest implements APIMessage
{
    private final Timestamp expiry;
    private final String ipAddress;
    private final String reason;
    private final String name;
    private final String type;
    private final int id;
    
    @Override
    public String getChannel() {
        return "Punish";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)new ImmutableMap.Builder().put((Object)"ip-address", (Object)((this.ipAddress == null) ? "UNKNOWN" : this.ipAddress)).put((Object)"expiry", (Object)((this.expiry == null) ? "PERM" : this.expiry.toString())).put((Object)"reason", (Object)this.reason).put((Object)"punisher", (Object)this.id).put((Object)"name", (Object)this.name).put((Object)"type", (Object)this.type).build();
    }
    
    @ConstructorProperties({ "expiry", "ipAddress", "reason", "name", "type", "id" })
    public PunishmentRequest(final Timestamp expiry, final String ipAddress, final String reason, final String name, final String type, final int id) {
        this.expiry = expiry;
        this.ipAddress = ipAddress;
        this.reason = reason;
        this.name = name;
        this.type = type;
        this.id = id;
    }
}
