package net.badlion.tournament.matches;

import net.badlion.tournament.teams.Team;

import java.util.UUID;

public class Round {

    private UUID id;
    private Series series;
    private Team winningTeam = null;
    private boolean edited = false;

    public Round(UUID id, Series series) {
        this(id, series, false);
    }

    public Round(UUID id, Series series, boolean edited) {
        this.id = id;
        this.series = series;
        series.getRounds().add(this);
        series.setEdited(edited);
    }

    public UUID getID() {
        return id;
    }

    public Series getSeries() {
        return series;
    }

    public Team getWinningTeam() {
        return winningTeam;
    }

    public void setWinningTeam(Team winningTeam) {
        this.winningTeam = winningTeam;
        this.setEdited(true);
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
