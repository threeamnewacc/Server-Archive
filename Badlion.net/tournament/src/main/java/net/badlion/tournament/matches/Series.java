package net.badlion.tournament.matches;

import net.badlion.tournament.teams.Team;

import java.util.*;

public class Series {

    private UUID id;
    private List<Round> rounds = new ArrayList<>();
    private int roundsToWin = 1; // 1 default
    private List<Team> teams = new ArrayList<>();
    private boolean started = false;
    private Map<Team, Integer> points = new HashMap<>();
    private Map<Team, Boolean> ready = new HashMap<>();
    private boolean edited = false;

    public Series(UUID id, int roundsToWin, Team... teams) {
        this.id = id;
        this.roundsToWin = roundsToWin;
        this.addTeams(teams);
    }

    public Series(UUID id, int roundsToWin, Set<Team> teams) {
        this.id = id;
        this.roundsToWin = roundsToWin;
        this.addTeams(teams);
    }

    public UUID getID() {
        return id;
    }

    public void addTeams(Team[] teams) {
        for (Team team : teams) {
            this.addTeam(team, true);
        }
    }

    public void addTeams(Set<Team> teams) {
        for (Team team : teams) {
            this.addTeam(team, false);
        }
    }

    public boolean addTeam(Team team, boolean edited) {
        this.getTeams().add(team);
        this.setPoints(team, 0, true);
        this.getReady().put(team, false);
        this.setEdited(edited);
        return true;
    }

    public boolean removeTeam(Team team, boolean edited) {
        this.getTeams().remove(team);
        this.getPoints().remove(team);
        this.getReady().remove(team);
        this.setEdited(edited);
        return true;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    public void setRounds(List<Round> rounds, boolean edited) {
        this.rounds = rounds;
        this.setEdited(edited);
    }

    public int getRoundsToWin() {
        return roundsToWin;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started, boolean edited) {
        this.started = started;
        this.setEdited(edited);
    }

    public Map<Team, Integer> getPoints() {
        return points;
    }

    public void setPoints(Map<Team, Integer> points, boolean edited) {
        this.points = points;
        this.setEdited(edited);
    }

    public void setPoints(Team team, int points, boolean edited) {
        this.getPoints().put(team, points);
        this.setEdited(edited);
    }

    public Map<Team, Boolean> getReady() {
        return ready;
    }

    public void setReady(Team team) {
        this.getReady().put(team, !this.getReady().get(team));
        this.setEdited(true);
    }

    public boolean allReady() {
        boolean allReady = true;

        for (Boolean ready : this.getReady().values()) {
            if (!ready) {
                allReady = false;
            }
        }
        return allReady;
    }

    public Team getWinningTeam() {
        for (Team team : getTeams()) {
            if (this.getPoints().get(team) == null || this.getPoints().get(team) >= this.getRoundsToWin()) {
                return team;
            }
        }
        return null;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
