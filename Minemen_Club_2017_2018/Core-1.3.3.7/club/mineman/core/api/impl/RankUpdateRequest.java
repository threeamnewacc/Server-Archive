// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.api.impl;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import club.mineman.core.util.finalutil.TimeUtil;
import java.util.Map;
import club.mineman.core.rank.Rank;
import club.mineman.core.api.APIMessage;

public class RankUpdateRequest implements APIMessage
{
    private final Rank rank;
    private final String name;
    private final long duration;
    private final int giver;
    
    @Override
    public String getChannel() {
        return "Update-Rank";
    }
    
    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>)ImmutableMap.of((Object)"rank", (Object)this.rank.getName(), (Object)"name", (Object)this.name, (Object)"given-by", (Object)this.giver, (Object)"start-time", (Object)TimeUtil.getCurrentTimestamp(), (Object)"end-time", (Object)TimeUtil.addDuration(this.duration));
    }
    
    @ConstructorProperties({ "rank", "name", "duration", "giver" })
    public RankUpdateRequest(final Rank rank, final String name, final long duration, final int giver) {
        this.rank = rank;
        this.name = name;
        this.duration = duration;
        this.giver = giver;
    }
}
