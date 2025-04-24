// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.net.InetAddress;
import club.mineman.core.api.APIMessage;

public class IPCheckRequest implements APIMessage
{
    private final InetAddress inetAddress;
    
    @Override
    public String getChannel() {
        return "IPCheck";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"ip-address", (Object)this.inetAddress.getHostAddress());
    }
    
    @ConstructorProperties({ "inetAddress" })
    public IPCheckRequest(final InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
}
