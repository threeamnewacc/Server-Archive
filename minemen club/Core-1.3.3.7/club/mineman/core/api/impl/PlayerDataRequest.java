// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import java.net.InetAddress;
import club.mineman.core.api.APIMessage;

public class PlayerDataRequest implements APIMessage
{
    private final PlayerDataRequestType requestType;
    private final InetAddress address;
    private final UUID uniqueId;
    private final String name;
    
    @Override
    public String getChannel() {
        return "PlayerData";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"request-type", (Object)this.requestType.name().toLowerCase(), (Object)"ip-address", (Object)this.address.getHostAddress(), (Object)"uuid", (Object)this.uniqueId.toString(), (Object)"name", (Object)this.name);
    }
    
    @ConstructorProperties({ "requestType", "address", "uniqueId", "name" })
    public PlayerDataRequest(final PlayerDataRequestType requestType, final InetAddress address, final UUID uniqueId, final String name) {
        this.requestType = requestType;
        this.address = address;
        this.uniqueId = uniqueId;
        this.name = name;
    }
    
    public enum PlayerDataRequestType
    {
        PUNISHMENTS, 
        VIOLATIONS, 
        PRACTICE_STATS, 
        PRACTICE_SETTINGS, 
        GLOBAL, 
        RANKS, 
        JOINS, 
        IGNORES;
    }
}
