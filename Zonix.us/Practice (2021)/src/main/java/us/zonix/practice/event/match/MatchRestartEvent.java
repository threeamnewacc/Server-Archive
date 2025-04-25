package us.zonix.practice.event.match;

import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;

public class MatchRestartEvent extends MatchEvent {
    private final MatchTeam winningTeam;
    private final MatchTeam losingTeam;

    public MatchRestartEvent(Match match, MatchTeam winningTeam, MatchTeam losingTeam) {
        super(match);
        this.winningTeam = winningTeam;
        this.losingTeam = losingTeam;
    }

    public MatchRestartEvent(Match match) {
        super(match);
        this.winningTeam = null;
        this.losingTeam = null;
    }

    public MatchTeam getWinningTeam() {
        return this.winningTeam;
    }

    public MatchTeam getLosingTeam() {
        return this.losingTeam;
    }
}
