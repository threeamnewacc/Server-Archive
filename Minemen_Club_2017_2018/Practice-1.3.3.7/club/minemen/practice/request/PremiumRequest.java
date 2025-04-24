// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.request;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import club.minemen.core.api.request.Request;

public class PremiumRequest implements Request
{
    private final String subCommand;
    private final String name;
    private final int amount;
    
    public String getPath() {
        return "/premium/" + this.subCommand + "/" + this.amount;
    }
    
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"sub-command", (Object)this.subCommand, (Object)"name", (Object)this.name, (Object)"amount", (Object)this.amount);
    }
    
    @ConstructorProperties({ "subCommand", "name", "amount" })
    public PremiumRequest(final String subCommand, final String name, final int amount) {
        this.subCommand = subCommand;
        this.name = name;
        this.amount = amount;
    }
}
