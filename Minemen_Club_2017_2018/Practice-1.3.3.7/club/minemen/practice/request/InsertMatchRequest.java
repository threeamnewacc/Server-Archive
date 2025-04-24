// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.request;

import java.beans.ConstructorProperties;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.UUID;
import club.minemen.core.api.request.Request;

public class InsertMatchRequest implements Request
{
    private final UUID matchId;
    private final Integer winners;
    private final Integer losers;
    private final int inventory;
    private final int[] eloBefore;
    private final int[] eloAfter;
    
    public String getPath() {
        return "/matches/insert/match";
    }
    
    public Map<String, Object> toMap() {
        return (Map<String, Object>)new ImmutableMap.Builder().put((Object)"match-id", (Object)this.matchId.toString()).put((Object)"inventory", (Object)this.inventory).put((Object)"winner-elo-before", (Object)this.eloBefore[0]).put((Object)"loser-elo-before", (Object)this.eloBefore[1]).put((Object)"winner-elo-after", (Object)this.eloAfter[0]).put((Object)"loser-elo-after", (Object)this.eloAfter[1]).put((Object)"winners", (Object)this.winners).put((Object)"losers", (Object)this.losers).build();
    }
    
    @ConstructorProperties({ "matchId", "winners", "losers", "inventory", "eloBefore", "eloAfter" })
    public InsertMatchRequest(final UUID matchId, final Integer winners, final Integer losers, final int inventory, final int[] eloBefore, final int[] eloAfter) {
        this.matchId = matchId;
        this.winners = winners;
        this.losers = losers;
        this.inventory = inventory;
        this.eloBefore = eloBefore;
        this.eloAfter = eloAfter;
    }
}
