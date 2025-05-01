package net.badlion.tournament.listeners;

import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.tournament.TournamentPlugin;
import net.badlion.tournament.TournamentStateMachine;
import net.badlion.tournament.bracket.SingleKnockoutBracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.events.bracket.MatchEndEvent;
import net.badlion.tournament.events.bracket.MatchStartEvent;
import net.badlion.tournament.events.bracket.SeriesEndEvent;
import net.badlion.tournament.events.bracket.SeriesStartEvent;
import net.badlion.tournament.events.tournament.TournamentEndEvent;
import net.badlion.tournament.events.tournament.TournamentStartEvent;
import net.badlion.tournament.matches.Round;
import net.badlion.tournament.matches.Series;
import net.badlion.tournament.teams.SoloTeam;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StateMachineListener implements Listener {

    @EventHandler(priority = EventPriority.FIRST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        TournamentPlugin instance = TournamentPlugin.getInstance();
        if (instance.getAutomatedTournament() != null) {
            if (Bukkit.getOnlinePlayers().size() <= instance.getAutomatedPlayerCount()) {
                Bukkit.broadcastMessage(Bukkit.getOnlinePlayers().size() + "/" + instance.getAutomatedPlayerCount() + " players have joined for the tournament!");
            }
            if (Bukkit.getOnlinePlayers().size() >= instance.getAutomatedPlayerCount()) {
                Bukkit.broadcastMessage("The tournament is starting!");

                TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();
                List<Team> teams = new ArrayList<>();

                if (instance.isTeamsEnabled()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (stateMachine.getTeam(player.getUniqueId()) == null) {
                            SoloTeam soloTeam = new SoloTeam(player.getUniqueId());
                            teams.add(soloTeam);
                            TournamentStateMachine.storeTeam(player.getUniqueId(), soloTeam);
                        } else {
                            if (!teams.contains(stateMachine.getTeam(player.getUniqueId()))) {
                                teams.add(stateMachine.getTeam(player.getUniqueId()));
                            }
                        }
                    }
                } else {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        SoloTeam soloTeam = new SoloTeam(player.getUniqueId());
                        teams.add(soloTeam);
                        TournamentStateMachine.storeTeam(player.getUniqueId(), soloTeam);
                    }
                }

                for (Team team : teams) {
                    stateMachine.getLobbyState().add(team, true);
                    stateMachine.setCurrentState(team, stateMachine.getLobbyState());
                }

                instance.getAutomatedTournament().getBracket().generate(teams, true);
            }
        }
    }

    @EventHandler
    public void onRoundStart(MatchStartEvent event) {
        SeriesNode node = event.getSeriesNode();
        Series series = node.getContent();
        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        for (Team team : series.getTeams()) {
            try {
                stateMachine.getCurrentState(team).transition(stateMachine.getMatchState(), team);
            } catch (IllegalStateTransitionException e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onRoundEnd(MatchEndEvent event) {
        SeriesNode node = event.getSeriesNode();
        Series series = node.getContent();
        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        for (Team team : series.getTeams()) {
            try {
                stateMachine.getCurrentState(team).transition(stateMachine.getSeriesState(), team);
            } catch (IllegalStateTransitionException e) {
                e.printStackTrace();
            }
        }

        event.getRound().setWinningTeam(event.getWinningTeam());

        if (series.getWinningTeam() != null) {
            TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new SeriesEndEvent(event.getTournament(), node));
        } else if (event.getTournament().getBracket().advancesAutomatically()) {
            TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new MatchStartEvent(event.getTournament(), node, new Round(UUID.randomUUID(), series)));
        }
    }

    @EventHandler
    public void onSeriesStart(SeriesStartEvent event) {
        SeriesNode node = event.getSeriesNode();
        Series series = node.getContent();
        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        StringBuilder sb = new StringBuilder();
        sb.append("Series started with: ");
        for (Team team : series.getTeams()) {
            if (series.getTeams().indexOf(team) + 1 < series.getTeams().size()) {
                sb.append(team.getName());
                sb.append(", ");
            } else if (series.getTeams().size() > 1) {
                sb.append("and ");
                sb.append(team.getName());
            } else {
                sb.append(team.getName());
            }
        }

        for (Team team : event.getSeriesNode().getContent().getTeams()) {
            try {
                stateMachine.getCurrentState(team).transition(stateMachine.getSeriesState(), team);
                team.sendMessage(ChatColor.BLUE + sb.toString());
            } catch (IllegalStateTransitionException e) {
                e.printStackTrace();
            }
        }

        TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new MatchStartEvent(event.getTournament(), node, new Round(UUID.randomUUID(), series)));
    }

    @EventHandler
    public void onSeriesEnd(SeriesEndEvent event) {
        SeriesNode node = event.getSeriesNode();
        Series series = node.getContent();
        TournamentStateMachine stateMachine = TournamentStateMachine.getInstance();

        for (Team team : event.getSeriesNode().getContent().getTeams()) {
            try {
                stateMachine.getCurrentState(team).transition(stateMachine.getLobbyState(), team);
            } catch (IllegalStateTransitionException e) {
                e.printStackTrace();
            }
        }

        for (Team team : event.getSeriesNode().getContent().getTeams()) {
            team.sendMessage(ChatColor.AQUA + series.getWinningTeam().getName() + " has won the series");
        }

        SingleKnockoutBracket.AdvanceState advanceState = event.getTournament().getBracket().advance(node);
        if (advanceState.equals(SingleKnockoutBracket.AdvanceState.BRACKET_END)) {
            TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new TournamentEndEvent(event.getTournament()));
        }
    }

    @EventHandler(priority = EventPriority.FIRST)
    public void onTournamentStart(TournamentStartEvent event) {
        Tournament tournament = event.getTournament();
        Bukkit.broadcastMessage(ChatColor.AQUA + "The " + tournament.getName() + " Tournament has started!");
    }

    @EventHandler(priority = EventPriority.FIRST)
    public void onTournamentEnd(TournamentEndEvent event) {
        Tournament tournament = event.getTournament();
        Bukkit.broadcastMessage(ChatColor.AQUA + "The " + tournament.getName() + " Tournament has ended! Congratulations to the winner: "
                + tournament.getBracket().getRoot().getContent().getWinningTeam().getName() + "!");
    }

}
