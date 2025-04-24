// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.request;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.json.simple.JSONArray;

public class AGCLogRequest extends AGCRequest
{
    private final JSONArray data;
    
    public AGCLogRequest(final JSONArray data) {
        super("insert-logs");
        this.data = data;
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)new ImmutableMap.Builder().put((Object)"data", (Object)this.data.toJSONString()).putAll((Map)super.toMap()).build();
    }
}
