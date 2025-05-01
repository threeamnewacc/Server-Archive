package net.badlion.tournament;

import net.badlion.tournament.commands.ReadyCommand;
import net.badlion.tournament.commands.TeamCommand;
import net.badlion.tournament.commands.TournamentCommand;
import net.badlion.tournament.listeners.StateMachineListener;
import net.badlion.tournament.teams.SoloTeam;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.RoundRobinTournament;
import net.badlion.tournament.tournaments.SingleKnockoutTournament;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class TournamentPlugin extends JavaPlugin {

    private static TournamentPlugin plugin;

    private List<Tournament> tournaments = new ArrayList<>();
    private int defaultPlayers = 16;
    private boolean teamsEnabled = false;
    private int maxTeamSize = 5;
    private String invitationType = "all";

    private Tournament automatedTournament = null;
    private int automatedPlayerCount = 16;

    public static TournamentPlugin getInstance() {
        return TournamentPlugin.plugin;
    }

    public TournamentPlugin() {
        TournamentPlugin.plugin = this;
    }

    @Override
    public void onEnable() {
        TournamentStateMachine stateMachine = new TournamentStateMachine();
        stateMachine.init();

        this.defaultPlayers = this.getConfig().getInt("tournament.default-players");
        this.teamsEnabled = this.getConfig().getBoolean("tournament.teams");
        this.maxTeamSize = this.getConfig().getInt("tournament.team-size");
        this.invitationType = this.getConfig().getString("tournament.invitation-type");
        this.automatedPlayerCount = this.getConfig().getInt("tournament.automated-player-count");
        String automatedTournamentType = this.getConfig().getString("tournament.automated-type");

        if (false) {
            UUID uuid = UUID.randomUUID();
            if (automatedTournamentType.equalsIgnoreCase("singleknockout")) {
                this.automatedTournament = new SingleKnockoutTournament(uuid, "Test", new ArrayList<Team>(), false, 2, new HashMap<Integer, Integer>(), 3, false);
            } else if (automatedTournamentType.equalsIgnoreCase("roundrobin")) {
                this.automatedTournament = new RoundRobinTournament(uuid, uuid.toString(), new ArrayList<Team>(), false, 2, new HashMap<Integer, Integer>(), 3, this.getConfig().getInt("tournament.automated-group-size"), false);
            }
            this.automatedTournament.getBracket().setRequiresReady(false);
            this.automatedTournament.getBracket().setAdvanceAutomatically(true);
            this.tournaments.add(this.automatedTournament);
        }

        this.getCommand("ready").setExecutor(new ReadyCommand());
        this.getCommand("team").setExecutor(new TeamCommand());
        this.getCommand("tournament").setExecutor(new TournamentCommand());

        this.getServer().getPluginManager().registerEvents(new StateMachineListener(), this);
    }

    public void onDisable() {

    }

    public List<Tournament> getTournaments() {
        return tournaments;
    }

    public Tournament getTournament(UUID uuid) {
        for (Tournament tournament : TournamentPlugin.getInstance().getTournaments()) {
            for (Team team : tournament.getTeams()) {
                if (team.hasUUID(uuid)) {
                    return tournament;
                }
            }
        }
        return null;
    }

    public Tournament getTournament(String name) {
        for (Tournament tournament : TournamentPlugin.getInstance().getTournaments()) {
            if (tournament.getName().equalsIgnoreCase(name)) {
                return tournament;
            }
        }
        return null;
    }

    public boolean isTeamsEnabled() {
        return teamsEnabled;
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    public String getInvitationType() {
        return invitationType;
    }

    public Tournament getAutomatedTournament() {
        return automatedTournament;
    }

    public int getAutomatedPlayerCount() {
        return automatedPlayerCount;
    }

    public void startSoloTournament(String name, List<UUID> players, boolean generateBracket) {
        List<Team> teams = new ArrayList<>();
        for (UUID uuid : players) {
            SoloTeam soloTeam = new SoloTeam(uuid);
            teams.add(soloTeam);
            TournamentStateMachine.storeTeam(uuid, soloTeam);
        }

        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        for (Team team : teams) {
            stateMachine.getLobbyState().add(team, true);
            stateMachine.setCurrentState(team, stateMachine.getLobbyState());
        }

        SingleKnockoutTournament tournament = new SingleKnockoutTournament(
                UUID.randomUUID(), name, teams, true, 2, new HashMap<Integer, Integer>(), 3, generateBracket);
        this.tournaments.add(tournament);
    }

    public void startSoloTournament(Tournament tournament) {
        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        for (Team team : tournament.getTeams()) {
            for (UUID uuid : team.getUUIDs()) {
                TournamentStateMachine.storeTeam(uuid, team);
            }
            stateMachine.getLobbyState().add(team, true);
            stateMachine.setCurrentState(team, stateMachine.getLobbyState());
        }

        this.tournaments.add(tournament);
    }

    public void startRoundRobinTournament(String name, List<UUID> players, int groupSize, boolean generateBracket) {
        List<Team> teams = new ArrayList<>();
        for (UUID uuid : players) {
            SoloTeam soloTeam = new SoloTeam(uuid);
            teams.add(soloTeam);
            TournamentStateMachine.storeTeam(uuid, soloTeam);
        }

        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        for (Team team : teams) {
            stateMachine.getLobbyState().add(team, true);
            stateMachine.setCurrentState(team, stateMachine.getLobbyState());
        }

        RoundRobinTournament tournament = new RoundRobinTournament(
                UUID.randomUUID(), name, teams, true, 2, new HashMap<Integer, Integer>(), 3, groupSize, generateBracket);
        this.tournaments.add(tournament);
    }

    public void startRoundRobinTournament(Tournament tournament) {
        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        for (Team team : tournament.getTeams()) {
            for (UUID uuid : team.getUUIDs()) {
                TournamentStateMachine.storeTeam(uuid, team);
            }
            stateMachine.getLobbyState().add(team, true);
            stateMachine.setCurrentState(team, stateMachine.getLobbyState());
        }

        this.tournaments.add(tournament);
    }

    public int getDefaultPlayers() {
        return defaultPlayers;
    }
}
