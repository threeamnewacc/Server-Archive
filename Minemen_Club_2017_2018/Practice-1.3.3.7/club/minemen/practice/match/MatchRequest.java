// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.match;

import java.beans.ConstructorProperties;
import club.minemen.practice.arena.Arena;
import java.util.UUID;

public class MatchRequest
{
    private final UUID requester;
    private final UUID requested;
    private final Arena arena;
    private final String kitName;
    private final boolean party;
    
    public UUID getRequester() {
        return this.requester;
    }
    
    public UUID getRequested() {
        return this.requested;
    }
    
    public Arena getArena() {
        return this.arena;
    }
    
    public String getKitName() {
        return this.kitName;
    }
    
    public boolean isParty() {
        return this.party;
    }
    
    @ConstructorProperties({ "requester", "requested", "arena", "kitName", "party" })
    public MatchRequest(final UUID requester, final UUID requested, final Arena arena, final String kitName, final boolean party) {
        this.requester = requester;
        this.requested = requested;
        this.arena = arena;
        this.kitName = kitName;
        this.party = party;
    }
}
