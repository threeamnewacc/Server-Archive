package net.badlion.tournament.tournaments;

import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.teams.Team;

import java.util.List;
import java.util.UUID;

public interface Tournament {

    /**
     * Get the ID of the tournament
     */
    UUID getID();

    /**
     * Get name of tournament
     */
    String getName();

    /**
     * Get the type of the tournament
     */
    String getType();

    /**
     * Get participating teams
     */
    List<Team> getTeams();

    /**
     * Get a team from the tournament
     */
    Team getTeam(UUID teamID);


    boolean isActive();

    /**
     * Get bracket of tournament
     */
    Bracket getBracket();

    /**
     * Set the bracket
     */
    void setBracket(Bracket bracket);

    /**
     * Get allowed players per team
     */
    int getPlayersPerTeam();

    /**
     * Get link to match
     */
    String getLinkToTournament();

    /**
     * Get number of remaining series (could be bo1)
     */
    int getRemainingMatches();
}
