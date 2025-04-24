// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.match;

import java.util.UUID;
import java.util.List;
import club.minemen.practice.team.KillableTeam;

public class MatchTeam extends KillableTeam
{
    private final List<Integer> playerIds;
    private final int teamID;
    
    public MatchTeam(final UUID leader, final List<UUID> players, final List<Integer> playerIds, final int teamID) {
        super(leader, players);
        this.playerIds = playerIds;
        this.teamID = teamID;
    }
    
    public List<Integer> getPlayerIds() {
        return this.playerIds;
    }
    
    public int getTeamID() {
        return this.teamID;
    }
}
