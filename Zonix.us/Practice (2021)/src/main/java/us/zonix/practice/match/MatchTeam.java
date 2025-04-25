package us.zonix.practice.match;

import java.util.List;
import java.util.UUID;
import us.zonix.practice.team.KillableTeam;

public class MatchTeam extends KillableTeam {
    private final int teamID;
    int matchWins = 0;

    public MatchTeam(UUID leader, List<UUID> players, int teamID) {
        super(leader, players);
        this.teamID = teamID;
    }

    public int getTeamID() {
        return this.teamID;
    }

    public void setMatchWins(int matchWins) {
        this.matchWins = matchWins;
    }

    public int getMatchWins() {
        return this.matchWins;
    }
}
