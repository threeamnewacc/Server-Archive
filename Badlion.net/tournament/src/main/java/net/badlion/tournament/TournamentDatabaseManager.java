package net.badlion.tournament;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.DatabaseUtil;
import net.badlion.statemachine.State;
import net.badlion.tournament.bracket.RoundRobinBracket;
import net.badlion.tournament.bracket.SingleKnockoutBracket;
import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.matches.Round;
import net.badlion.tournament.matches.Series;
import net.badlion.tournament.states.TeamState;
import net.badlion.tournament.teams.DefaultTeam;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.RoundRobinTournament;
import net.badlion.tournament.tournaments.SingleKnockoutTournament;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class TournamentDatabaseManager {

    private static String tablePrefix = "tournaments";

    public static void insertTournament(final Tournament tournament) {
        final String query = "INSERT INTO " + getTablePrefix() + " (tournament_id, name, type, active) VALUES (?, ?, ?, ?);";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.insertDB(query, tournament.getID().toString(), tournament.getName(), tournament.getType(), tournament.isActive());
                insertTeams(tournament, tournament.getTeams());
                insertBracket(tournament.getBracket());
                insertTeamStates(TournamentStateMachine.getInstance());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void deleteTournament(final Tournament tournament) {
        final String query = "DELETE FROM " + getTablePrefix() + " WHERE tournament_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, tournament.getID().toString());
                deleteTeams(tournament.getTeams());
                deleteBracket(tournament.getBracket());
                deleteTeamStates(TournamentStateMachine.getInstance());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateTournament(final Tournament tournament) {
        deleteTournament(tournament);
        insertTournament(tournament);
        new BukkitRunnable() {
            public void run() {
                updateTeams(tournament, tournament.getTeams());
                updateBracket(tournament.getBracket());
                updateTeamStates(TournamentStateMachine.getInstance());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static Tournament getTournament(final UUID tournamentID) {
        String query = "SELECT * FROM " + getTablePrefix() + " WHERE tournament_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Tournament tournament = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, tournamentID.toString());

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                if (rs.getString("type").equalsIgnoreCase("singleknockout")) {
                    List<Team> teams = getTeams(tournamentID);
                    tournament = new SingleKnockoutTournament(
                            tournamentID, rs.getString("name"), teams, rs.getBoolean("active"), getBracket(tournamentID, rs.getString("type"), teams));
                    tournament.getBracket().setTournament(tournament);
                    Map<Team, String> teamStates = getTeamStates(tournament);
                    for (Team team : teamStates.keySet()) {
                        State<Team> state = TournamentStateMachine.getInstance().getState(teamStates.get(team));
                        state.add(team, true);
                        TournamentStateMachine.getInstance().setCurrentState(team, state);
                    }
                } else if (rs.getString("type").equalsIgnoreCase("roundrobin")) {
                    List<Team> teams = getTeams(tournamentID);
                    tournament = new RoundRobinTournament(
                            tournamentID, rs.getString("name"), teams, rs.getBoolean("active"), getBracket(tournamentID, rs.getString("type"), teams));
                    tournament.getBracket().setTournament(tournament);
                    Map<Team, String> teamStates = getTeamStates(tournament);
                    for (Team team : teamStates.keySet()) {
                        State<Team> state = TournamentStateMachine.getInstance().getState(teamStates.get(team));
                        state.add(team, true);
                        TournamentStateMachine.getInstance().setCurrentState(team, state);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return tournament;
    }

    public static Tournament getTournament(final String name) {
        String query = "SELECT * FROM " + getTablePrefix() + " WHERE name = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Tournament tournament = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, name);

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                UUID tournamentID = UUID.fromString(rs.getString("tournament_id"));
                if (rs.getString("type").equalsIgnoreCase("singleknockout")) {
                    List<Team> teams = getTeams(tournamentID);
                    tournament = new SingleKnockoutTournament(
                            tournamentID, rs.getString("name"), teams, rs.getBoolean("active"), getBracket(tournamentID, "singleknockout", teams));
                    tournament.getBracket().setTournament(tournament);
                    Map<Team, String> teamStates = getTeamStates(tournament);
                    for (Team team : teamStates.keySet()) {
                        TeamState<Team> state = TournamentStateMachine.getInstance().getState(teamStates.get(team));
                        state.add(team, true);
                        TournamentStateMachine.getInstance().setCurrentState(team, state);

                    }
                } else if (rs.getString("type").equalsIgnoreCase("roundrobin")) {
                    List<Team> teams = getTeams(tournamentID);
                    tournament = new RoundRobinTournament(
                            tournamentID, rs.getString("name"), teams, rs.getBoolean("active"), getBracket(tournamentID, "roundrobin", teams));
                    tournament.getBracket().setTournament(tournament);
                    Map<Team, String> teamStates = getTeamStates(tournament);
                    for (Team team : teamStates.keySet()) {
                        State<Team> state = TournamentStateMachine.getInstance().getState(teamStates.get(team));
                        state.add(team, true);
                        TournamentStateMachine.getInstance().setCurrentState(team, state);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return tournament;
    }

    public static boolean tournamentExists(final UUID tournamentID) {
        String query = "SELECT * FROM " + getTablePrefix() + " WHERE tournament_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean tournamentExists = false;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, tournamentID.toString());

            rs = Gberry.executeQuery(connection, ps);

            try {
                if (rs.next()) {
                    tournamentExists = true;
                }
            } catch (Exception e) {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return tournamentExists;
    }

    public static void insertTeams(final Tournament tournament, final List<Team> teams) {
        for (Team team : teams) {
            insertTeam(tournament, team);
            insertTeamPlayers(team);
        }
    }

    public static void insertTeam(final Tournament tournament, final Team team) {
        String query = "INSERT INTO " + getTablePrefix() + "_teams (team_id, name, tournament_id, leader) VALUES (?, ?, ?, ?);";
        DatabaseUtil.insertDB(query, team.getID().toString(), team.getName(), tournament.getID().toString(), team.getLeader());
    }

    public static void deleteTeams(final List<Team> teams) {
        for (Team team : teams) {
            deleteTeam(team);
            deleteTeamPlayers(team);
        }
    }

    public static void deleteTeam(final Team team) {
        final String query = "DELETE FROM " + getTablePrefix() + "_teams WHERE team_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, team.getID().toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateTeams(final Tournament tournament, final List<Team> teams) {
        for (Team team : teams) {
            updateTeam(tournament, team);
            updateTeamPlayers(team);
        }
    }

    public static void updateTeam(final Tournament tournament, final Team team) {
        deleteTeam(team);
        insertTeam(tournament, team);
    }

    public static List<Team> getTeams(final UUID tournamentID) {
        String query = "SELECT * FROM " + getTablePrefix() + "_teams WHERE tournament_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Team> teams = new ArrayList<>();

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, tournamentID.toString());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                Team team = new DefaultTeam(UUID.fromString(rs.getString("team_id")), rs.getString("name"), UUID.fromString(rs.getString("leader")));
                Map<UUID, String> roles = getTeamPlayers(team);
                team.setRoles(roles);
                team.setUUIDs(roles.keySet());
                teams.add(team);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return teams;
    }

    public static Team getTeam(final UUID teamID) {
        String query = "SELECT * FROM " + getTablePrefix() + "_teams WHERE team_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Team team = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, teamID.toString());

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                team = new DefaultTeam(teamID, rs.getString("name"), UUID.fromString(rs.getString("leader")));
                Map<UUID, String> roles = getTeamPlayers(team);
                team.setRoles(roles);
                team.setUUIDs(roles.keySet());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return team;
    }

    public static void insertTeamPlayers(final Team team) {
        for (UUID uuid : team.getUUIDs()) {
            insertTeamPlayer(team, uuid, team.getRole(uuid));
        }
    }

    public static void insertTeamPlayer(final Team team, final UUID uuid, final String role) {
        String query = "INSERT INTO " + getTablePrefix() + "_teams_roster (team_id, member, role) VALUES (?, ?, ?);";
        DatabaseUtil.insertDB(query, team.getID().toString(), uuid.toString(), role);
    }

    public static void deleteTeamPlayers(final Team team) {
        final String query = "DELETE FROM " + getTablePrefix() + "_teams_roster WHERE team_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, team.getID().toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void deleteTeamPlayer(final Team team, final UUID uuid) {
        final String query = "DELETE FROM " + getTablePrefix() + "_teams_roster WHERE team_id = ? AND member = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, team.getID().toString(), uuid.toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateTeamPlayers(final Team team) {
        for (UUID uuid : team.getUUIDs()) {
            updateTeamPlayer(team, uuid, team.getRole(uuid));
        }
    }

    public static void updateTeamPlayer(final Team team, final UUID uuid, final String role) {
        deleteTeamPlayer(team, uuid);
        insertTeamPlayer(team, uuid, role);
    }

    public static Map<UUID, String> getTeamPlayers(final Team team) {
        String query = "SELECT * FROM " + getTablePrefix() + "_teams_roster WHERE team_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<UUID, String> uuids = new HashMap<>();

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, team.getID().toString());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("member"));
                uuids.put(uuid, rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return uuids;
    }

    public static void insertTeamStates(final TournamentStateMachine stateMachine) {
        for (State<Team> state : stateMachine.getStates()) {
            insertTeamState(state);
        }
    }

    public static void insertTeamState(final State<Team> state) {
        final String query = "INSERT INTO " + getTablePrefix() + "_teams_state (team_id, state) VALUES (?, ?);";
        for (final Team team : state.getElements()) {
            insertTeamState(state, team);
        }
    }

    public static void insertTeamState(final State<Team> state, final Team team) {
        final String query = "INSERT INTO " + getTablePrefix() + "_teams_state (team_id, state) VALUES (?, ?);";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.insertDB(query, team.getID().toString(), state.getStateName());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void deleteTeamStates(final TournamentStateMachine stateMachine) {
        for (State<Team> state : stateMachine.getStates()) {
            deleteTeamState(state);
        }
    }

    public static void deleteTeamState(final State<Team> state) {
        for (Team team : state.getElements()) {
            deleteTeamState(team);
        }
    }

    public static void deleteTeamState(final Team team) {
        final String query = "DELETE FROM " + getTablePrefix() + "_teams_state WHERE team_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, team.getID().toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }


    public static void updateTeamStates(final TournamentStateMachine stateMachine) {
        for (State<Team> state : stateMachine.getStates()) {
            updateTeamState((TeamState<Team>)state);
        }
    }

    public static void updateTeamState(final TeamState<Team> state) {
        for (Team team : state.getElements()) {
            if (state.isEdited(team)) {
                deleteTeamState(team);
                insertTeamState(state, team);
            }
        }
    }

    public static Map<Team, String> getTeamStates(final Tournament tournament) {
        Map<Team, String> teamStates = new HashMap<>();

        for (Team team : getTeams(tournament.getID())) {
            teamStates.put(team, getTeamState(team));
        }

        return teamStates;
    }

    public static String getTeamState(final Team team) {
        String state = "lobby";

        String query = "SELECT * FROM " + getTablePrefix() + "_teams_state WHERE team_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, team.getID().toString());

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                state = rs.getString("state");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return state;
    }
    public static void insertBracket(final Bracket bracket) {
        String query = "INSERT INTO " + getTablePrefix() + "_bracket (bracket_id, tournament_id, teams_per_match, default_rounds_to_win) VALUES (?, ?, ?, ?);";
        DatabaseUtil.insertDB(query, bracket.getID().toString(), bracket.getTournament().getID().toString(), bracket.getTeamsPerMatch(), bracket.getDefaultRoundsToWin());
        Map<Integer, Integer> roundsToWin = bracket.getRoundsToWin();
        for (Integer round : roundsToWin.keySet()) {
            insertRoundsToWin(bracket, round, roundsToWin.get(round));
        }
        for (SeriesNode node : bracket.getNodes()) {
            insertBracketNode(node);
        }
    }

    public static void deleteBracket(final Bracket bracket) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket WHERE bracket_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, bracket.getID().toString());
                deleteRoundsToWin(bracket);
                for (SeriesNode node : bracket.getNodes()) {
                    deleteBracketNode(node);
                }
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateBracket(final Bracket bracket) {
        if (bracket.isEdited()) {
            deleteBracket(bracket);
            insertBracket(bracket);
            Map<Integer, Integer> roundsToWin = bracket.getRoundsToWin();
            for (Integer round : roundsToWin.keySet()) {
                updateRoundsToWin(bracket, round, roundsToWin.get(round));
            }
        }
        for (SeriesNode node : bracket.getNodes()) {
            updateBracketNode(node);
        }
    }

    public static Bracket getBracket(final UUID tournamentID, final String type, final List<Team> teams) {
        String query = "SELECT * FROM " + getTablePrefix() + "_bracket WHERE tournament_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Bracket bracket = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, tournamentID.toString());

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                if (type.equalsIgnoreCase("singleknockout")) {
                    UUID bracketID = UUID.fromString(rs.getString("bracket_id"));
                    bracket = new SingleKnockoutBracket(
                            bracketID, rs.getInt("teams_per_match"), rs.getInt("default_rounds_to_win"), getRoundsToWin(bracketID), false);
                    bracket.setNodes(getBracketNodes(bracket, teams));
                } else if (type.equalsIgnoreCase("roundrobin")) {
                    UUID bracketID = UUID.fromString(rs.getString("bracket_id"));
                    bracket = new RoundRobinBracket(
                            bracketID, rs.getInt("teams_per_match"), rs.getInt("default_rounds_to_win"), getRoundsToWin(bracketID));
                    bracket.setNodes(getBracketNodes(bracket, teams));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return bracket;
    }

    public static void insertRoundsToWin(final Bracket bracket, int round, int winsRequired) {
        String query = "INSERT INTO " + getTablePrefix() + "_bracket_rounds_to_win (bracket_id, round, wins_required) VALUES (?, ?, ?);";
        DatabaseUtil.insertDB(query, bracket.getID().toString(), round, winsRequired);
    }

    public static void deleteRoundsToWin(final Bracket bracket) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket_rounds_to_win WHERE bracket_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, bracket.getID().toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void deleteRoundToWin(final Bracket bracket, final int round) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket_rounds_to_win WHERE bracket_id = ? AND round = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, bracket.getID().toString(), round);
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateRoundsToWin(final Bracket bracket, final int round, final int winsRequired) {
        deleteRoundToWin(bracket, round);
        insertRoundsToWin(bracket, round, winsRequired);
    }

    public static Map<Integer, Integer> getRoundsToWin(final UUID bracketID) {
        String query = "SELECT * FROM " + getTablePrefix() + "_bracket_rounds_to_win WHERE bracket_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Integer, Integer> roundsToWin = new HashMap<>();

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, bracketID.toString());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                roundsToWin.put(rs.getInt("round"), rs.getInt("wins_required"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return roundsToWin;
    }

    public static void insertBracketNode(final SeriesNode node) {
        String query = "INSERT INTO " + getTablePrefix() + "_bracket_nodes (node_id, bracket_id, parent_node_id, root, series) VALUES (?, ?, ?, ?, ?);";
        UUID parentID = null;
        try {
            if (node.getParent() != null) {
                parentID = node.getParent().getID();
            }
        } catch (Exception e) {
        }

        DatabaseUtil.insertDB(query, node.getID().toString(), node.getBracket().getID().toString(), parentID, node.isRoot(), node.getSeries());
        insertSeries(node, node.getContent());
    }

    public static void deleteBracketNode(final SeriesNode node) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket_nodes WHERE node_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, node.getID().toString());
                deleteSeries(node.getContent());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateBracketNode(final SeriesNode node) {
        if (node.isEdited()) {
            deleteBracketNode(node);
            insertBracketNode(node);
        }
        updateSeries(node, node.getContent());
    }

    public static List<SeriesNode> getBracketNodes(final Bracket bracket, final List<Team> teams) {
        String query = "SELECT * FROM " + getTablePrefix() + "_bracket_nodes WHERE bracket_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<SeriesNode> nodes = new ArrayList<>();

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, bracket.getID().toString());

            rs = Gberry.executeQuery(connection, ps);
            Map<SeriesNode, UUID> nodeParentID = new HashMap<>();

            while (rs.next()) {
                UUID nodeID = UUID.fromString(rs.getString("node_id"));
                SeriesNode node = new SeriesNode(nodeID, getSeries(nodeID, teams), bracket, null, rs.getInt("series"), false);
                nodes.add(node);
                if (rs.getBoolean("root")) {
                    bracket.setRoot(node);
                }
                try {
                    nodeParentID.put(node, UUID.fromString(rs.getString("parent_node_id")));
                } catch (Exception e) {
                    //null parent - either root or bug
                }
            }

            for (SeriesNode node : nodes) {
                for (SeriesNode node2 : nodes) {
                    try {
                        if (nodeParentID.get(node).equals(node2.getID())) {
                            node.setParent(node2);
                        }
                    } catch (Exception e) {
                        //null parent - either root or bug
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return nodes;
    }

    public static void insertSeries(final SeriesNode node, final Series series) {
        String query = "INSERT INTO " + getTablePrefix() + "_bracket_series (series_id, node_id, rounds_to_win, started) VALUES (?, ?, ?, ?);";
        DatabaseUtil.insertDB(query, series.getID().toString(), node.getID().toString(), series.getRoundsToWin(), series.isStarted());
        for (Team team : series.getPoints().keySet()) {
            try {
                insertSeriesPoints(series, team, series.getPoints().get(team));
            } catch (Exception e) {

            }
        }
        for (Round round : series.getRounds()) {
            try {
                insertSeriesRound(round);
            } catch (Exception e) {

            }
        }
    }

    public static void deleteSeries(final Series series) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket_series WHERE series_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, series.getID().toString());
                deleteSeriesPoints(series);
                for (Round round : series.getRounds()) {
                    deleteSeriesRound(round);
                }
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateSeries(final SeriesNode node, final Series series) {
        if (series.isEdited()) {
            deleteSeries(series);
            insertSeries(node, series);
            for (Team team : series.getPoints().keySet()) {
                updateSeriesPoints(series, team, series.getPoints().get(team));
            }
            for (Round round : series.getRounds()) {
                updateSeriesRound(round);
            }
        }
    }

    public static Series getSeries(final UUID nodeID, final List<Team> teams) {
        String query = "SELECT * FROM " + getTablePrefix() + "_bracket_series WHERE node_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Series series = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, nodeID.toString());

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                UUID seriesID = UUID.fromString(rs.getString("series_id"));
                Map<Team, Integer> points = getSeriesPoints(seriesID, teams);
                series = new Series(seriesID, rs.getInt("rounds_to_win"), points.keySet());
                series.setStarted(rs.getBoolean("started"), false);
                series.setPoints(points, false);
                series.setRounds(getSeriesRounds(series), false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return series;
    }

    public static void insertSeriesPoints(final Series series, final Team team, final int points) {
        String query = "INSERT INTO " + getTablePrefix() + "_bracket_series_points (series_id, team_id, points) VALUES (?, ?, ?);";
        DatabaseUtil.insertDB(query, series.getID().toString(), team.getID().toString(), points);
    }

    public static void deleteSeriesPoints(final Series series) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket_series_points WHERE series_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, series.getID().toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void deleteSeriesPoints(final Series series, final Team team) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket_series_points WHERE series_id = ? AND team_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, series.getID().toString(), team.getID().toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateSeriesPoints(final Series series, final Team team, final int points) {
        if (series.isEdited()) {
            deleteSeriesPoints(series, team);
            insertSeriesPoints(series, team, points);
        }
    }

    public static Map<Team, Integer> getSeriesPoints(final UUID seriesID, final List<Team> teams) {
        String query = "SELECT * FROM " + getTablePrefix() + "_bracket_series_points WHERE series_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<Team, Integer> points = new HashMap<Team, Integer>();

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, seriesID.toString());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                for (Team team : teams) {
                    if (team.getID().equals(UUID.fromString(rs.getString("team_id")))) {
                        points.put(team, rs.getInt("points"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return points;
    }

    public static void insertSeriesRound(final Round round) {
        UUID winningTeamID = null;
        try {
            if (round.getWinningTeam() != null) {
                winningTeamID = round.getWinningTeam().getID();
            }
        } catch (Exception e) {

        }
        String query = "INSERT INTO " + getTablePrefix() + "_bracket_series_rounds (series_id, round_id, winning_team_id) VALUES (?, ?, ?);";
        DatabaseUtil.insertDB(query, round.getSeries().getID().toString(), round.getID().toString(), winningTeamID);
    }

    public static void deleteSeriesRound(final Round round) {
        final String query = "DELETE FROM " + getTablePrefix() + "_bracket_series_rounds WHERE round_id = ?;";
        new BukkitRunnable() {
            public void run() {
                DatabaseUtil.execute(query, round.getID().toString());
            }
        }.runTaskAsynchronously(TournamentPlugin.getInstance());
    }

    public static void updateSeriesRound(final Round round) {
        if (round.isEdited()) {
            deleteSeriesRound(round);
            insertSeriesRound(round);
        }
    }

    public static List<Round> getSeriesRounds(final Series series) {
        String query = "SELECT * FROM " + getTablePrefix() + "_bracket_series_rounds WHERE series_id = ?;";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Round> rounds = new ArrayList<>();

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setObject(1, series.getID().toString());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                Round round = new Round(UUID.fromString(rs.getString("round_id")), series, false);
                try {
                    if (UUID.fromString(rs.getString("winning_team_id")) != null) {
                        round.setWinningTeam(getTeam(UUID.fromString(rs.getString("winning_team_id"))));
                    }
                } catch (Exception e) {

                }
                rounds.add(round);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return rounds;
    }

    public static String getTablePrefix() {
        return tablePrefix;
    }
}
