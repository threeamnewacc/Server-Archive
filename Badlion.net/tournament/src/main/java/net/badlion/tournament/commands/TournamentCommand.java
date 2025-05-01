package net.badlion.tournament.commands;

import net.badlion.gberry.Gberry;
import net.badlion.tournament.TournamentDatabaseManager;
import net.badlion.tournament.TournamentPlugin;
import net.badlion.tournament.TournamentStateMachine;
import net.badlion.tournament.bracket.RoundRobinBracket;
import net.badlion.tournament.bracket.filter.ActiveUUIDFilter;
import net.badlion.tournament.bracket.filter.SeriesIDFilter;
import net.badlion.tournament.bracket.filter.TeamFilter;
import net.badlion.tournament.bracket.filter.WinFilter;
import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.matches.Series;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.RoundRobinTournament;
import net.badlion.tournament.tournaments.SingleKnockoutTournament;
import net.badlion.tournament.tournaments.Tournament;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TournamentCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
        if (args.length >= 3 && args[0].equalsIgnoreCase("start")) { //tournament start
            List<UUID> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getUniqueId());
            }
            if (args[1].equalsIgnoreCase("solo")) { //tournament start solo <tournament>
                TournamentPlugin.getInstance().startSoloTournament(args[2], players, true);
                sender.sendMessage(ChatColor.GREEN + "Solo Tournament started!");
            }
            if (args[1].equalsIgnoreCase("roundrobin")) { //tournament start roundrobin <tournament> <group size>
                TournamentPlugin.getInstance().startRoundRobinTournament(args[2], players, Integer.parseInt(args[3]), true);
                sender.sendMessage(ChatColor.GREEN + "Round Robin Tournament started!");
            }
            if (args[1].equalsIgnoreCase("test")) { //tournament start test <number of fake players> <tournament>
                for (int i = 0; i <= Integer.parseInt(args[2]); i++) {
                    players.add(UUID.randomUUID());
                }
                TournamentPlugin.getInstance().startRoundRobinTournament(args[3], players, Integer.parseInt(args[4]), true);
                sender.sendMessage(ChatColor.GREEN + "Test Tournament started!");
            } else if (args.length == 1) {
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
                sender.sendMessage("/tournament start solo <name>");
                sender.sendMessage("/tournament start roundrobin <name> <group size>");
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
            }
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("end")) { //tournament end <tournament>
            if (TournamentPlugin.getInstance().getTournament(args[1]) != null) {
                //TODO End tournaments. Remove active series, cancel matches, etc. Should be a method in Tournament
                sender.sendMessage(ChatColor.GREEN + "Tournament ended!");
            }
        } else if (args.length >= 5 && args[0].equalsIgnoreCase("series")) {//tournament series
            Tournament tournament = TournamentPlugin.getInstance().getTournament(args[3]);
            Bracket bracket = tournament.getBracket();
            Map<SeriesNode, Team> nodes = new HashMap<>();
            if (tournament != null) {
                if (args[1].equalsIgnoreCase("position")) { //tournament series position <node> <tournament> <action> [team]
                    SeriesNode node = bracket.getNodes().get(Integer.parseInt(args[2]));
                    Team team = node.getContent().getTeams().get(0);
                    if (args.length >= 6) {
                        team = node.getContent().getTeams().get(Integer.parseInt(args[5]));
                    }
                    nodes.put(node, team);
                }
                if (args[1].equalsIgnoreCase("uuid")) { //tournament series uuid <uuid> <tournament> <action>
                    UUID uuid = UUID.fromString(args[2]);
                    List<SeriesNode> playerNodes = bracket.search(new ActiveUUIDFilter(bracket, uuid));
                    Team team = TournamentStateMachine.getTeam(uuid);
                    for (SeriesNode node : playerNodes) {
                        nodes.put(node, team);
                    }
                }
                if (args[1].equalsIgnoreCase("player")) { //tournament series player <player> <tournament> <action>
                    //TODO convert player name to uuid and follow uuid code
                }
                if (args[1].equalsIgnoreCase("team")) { //tournament series team <team> <tournament> <action>
                    Team team = TournamentStateMachine.getTeam(args[2]);
                    List<SeriesNode> teamNodes = bracket.search(new TeamFilter(bracket, team));
                    for (SeriesNode node : teamNodes) {
                        nodes.put(node, team);
                    }
                }
                for (SeriesNode node : nodes.keySet()) {
                    Team team = nodes.get(node);
                    if (args[4].equalsIgnoreCase("start")) { //tournament series <selector> <selected> <tournament> start
                        if (bracket.startSeries(node, true)) {
                            sender.sendMessage(ChatColor.GREEN + "Started series for team " + team.getName());
                        }
                    } else if (args[4].equalsIgnoreCase("advance")) { //tournament series <selector> <selected> <tournament> advance
                        if (bracket.endSeries(node, team)) {
                            sender.sendMessage(ChatColor.GREEN + "Advanced team " + team.getName());
                        }
                    } else if (args[4].equalsIgnoreCase("remove")) { //tournament series <selector> <selected> <tournament> remove
                        if (node.getContent().removeTeam(team, true)) {
                            sender.sendMessage(ChatColor.GREEN + "Removed team " + team.getName());
                        }
                    } else if (args[4].equalsIgnoreCase("add")) { //tournament series <selector> <selected> <tournament> add
                        if (node.getContent().addTeam(team, true)) {
                            sender.sendMessage(ChatColor.GREEN + "Added team " + team.getName());
                        }
                    }
                }
            } else if (args.length == 1) {
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
                sender.sendMessage("/tournament series position <node position> <tournament> start/advance/remove/add [team]");
                sender.sendMessage("/tournament series uuid <team uuid> <tournament> start/advance/remove/add [team]");
                sender.sendMessage("/tournament series team <team name> <tournament> start/advance/remove/add [team]");
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
            }
        } else if (args.length >= 4 && args[0].equalsIgnoreCase("print")) { //tournament print
            Tournament tournament = TournamentPlugin.getInstance().getTournament(args[2]);
            Bracket bracket = tournament.getBracket();
            if (tournament != null) {
                if (args[1].equalsIgnoreCase("info")) { //tournament print info
                    sender.sendMessage("Tournament: " + tournament.getName());
                    List<SeriesNode> nodes = new ArrayList<>();
                    if (args[3].equalsIgnoreCase("all")) { //tournament print info <tournament> all
                        nodes.addAll(bracket.getNodes());
                    } else if (args[3].equalsIgnoreCase("active")) { //tournament print info <tournament> active
                        nodes.addAll(bracket.getActiveNodes());
                    }
                    for (SeriesNode node : nodes) {
                        Series series = node.getContent();
                        sender.sendMessage("Node: " + bracket.getNodes().indexOf(node));

                        sender.sendMessage("  Round: " + node.getSeries() +
                                " | Started: " + series.isStarted() +
                                " | Active: " + bracket.isActive(node) +
                                " | ID: " + node.getID() +
                                " | Edited: " + node.getContent().isEdited());

                        //sender.sendMessage("  Teams: ");
                        for (Team team : series.getTeams()) {
                            String prefix = series.getWinningTeam() != null && series.getWinningTeam().equals(team) ? ChatColor.GREEN.toString() : ChatColor.RED.toString();
                            sender.sendMessage("    " + prefix + team.getName());
                        }
                    }
                } else if (args[1].equalsIgnoreCase("nodes")) { //tournament print nodes
                    sender.sendMessage("Tournament: " + tournament.getName());
                    this.printNode(sender, bracket.getRoot(), "");
                }
            } else if (args.length == 1) {
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
                sender.sendMessage("/tournament print info <tournament> all/active");
                sender.sendMessage("/tournament print nodes <tournament>");
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
            }
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("options")) { //tournament options
            Tournament tournament = TournamentPlugin.getInstance().getTournament(args[1]);
            Bracket bracket = tournament.getBracket();
            if (args.length >= 4 && args[2].equalsIgnoreCase("auto-advance")) { //tournament options <tournament> auto-advance <value>
                boolean value = Boolean.parseBoolean(args[3]);
                bracket.setAdvanceAutomatically(value);
                sender.sendMessage(ChatColor.GREEN + "Set tournament auto advancing to: " + value);
            } else if (args.length >= 4 && args[2].equalsIgnoreCase("require-ready")) { //tournament options <tournament> auto-advance <value>
                boolean value = Boolean.parseBoolean(args[3]);
                bracket.setRequiresReady(value);
                sender.sendMessage(ChatColor.GREEN + "Set tournament requiring /ready to: " + value);
            } else if (args.length >= 2) { //tournament options <tournament>
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
                sender.sendMessage(ChatColor.GREEN + "Auto Advance: " + bracket.advancesAutomatically());
                sender.sendMessage(ChatColor.GREEN + "Require /Ready: " + bracket.requiresReady());
                sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
            }
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("view")) { //tournament debug
            sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
            for (Tournament tournament : TournamentPlugin.getInstance().getTournaments()) {
                sender.sendMessage(tournament.getName());
            }
            sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("debug")) { //tournament debug
            sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
            Tournament tournament = TournamentPlugin.getInstance().getTournament(args[1]);
            Bracket bracket = tournament.getBracket();
            for (SeriesNode node : new SeriesIDFilter(bracket, ((RoundRobinBracket)bracket).getSeries()).filter()) {
                Bukkit.broadcastMessage(((RoundRobinBracket)bracket).getSeries() + "");
                Team winningTeam = null;
                for (Team team : node.getContent().getTeams()) {
                    if (team.getLeader() != null) {
                        winningTeam = team;
                    }
                }
                bracket.endSeries(node, winningTeam);
            }
            for (Team team : bracket.getTournament().getTeams()) {
                sender.sendMessage(team.getName() + " " + new WinFilter(bracket, team).filter().size());
            }
            sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("save")) { //tournament save <tournament>
            final Tournament tournament = TournamentPlugin.getInstance().getTournament(args[1]);
            new BukkitRunnable() {
                public void run() {
                    if (TournamentDatabaseManager.tournamentExists(tournament.getID())) {
                        TournamentDatabaseManager.updateTournament(tournament);
                        Bukkit.broadcastMessage(ChatColor.AQUA + "The " + tournament.getName() + " tournament is now updated in the database.");
                    } else {
                        TournamentDatabaseManager.insertTournament(tournament);
                        Bukkit.broadcastMessage(ChatColor.AQUA + "The " + tournament.getName() + " tournament is now added to the database.");
                    }
                }
            }.runTaskAsynchronously(TournamentPlugin.getInstance());
        } else if (args.length >= 2 && args[0].equalsIgnoreCase("load")) { //tournament load <tournament>
            new BukkitRunnable() {
                public void run() {
                    Tournament tournament;

                    if (args[1].contains("-") && args[1].length() > 16) {
                        tournament = TournamentDatabaseManager.getTournament(UUID.fromString(args[1]));
                    } else {
                        tournament = TournamentDatabaseManager.getTournament(args[1]);
                    }

                    if (tournament instanceof SingleKnockoutTournament) {
                        TournamentPlugin.getInstance().startSoloTournament(tournament);
                    } else if (tournament instanceof RoundRobinTournament) {
                        TournamentPlugin.getInstance().startRoundRobinTournament(tournament);
                    }
                    Bukkit.broadcastMessage(ChatColor.AQUA + "The " + args[1] + " tournament is now loaded.");
                }
            }.runTaskAsynchronously(TournamentPlugin.getInstance());
        } else if (args.length >= 1 && args[0].equalsIgnoreCase("help")) { //tournament help
            sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
            sender.sendMessage("/tournament start - Start a new tournament");
            sender.sendMessage("/tournament series - Edit series");
            sender.sendMessage("/tournament print - Look at information about series");
            sender.sendMessage("/tournament options - Change settings of the tournament");
            sender.sendMessage("/tournament view - Look at all running tournaments");
            sender.sendMessage("/tournament save <tournament> - Save a tournament to the database");
            sender.sendMessage("/tournament load <tournament> - Load a tournament from the database");
            sender.sendMessage(Gberry.getLineSeparator(ChatColor.AQUA));
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid usage.");
        }
        return true;
    }

    public void printNode(CommandSender sender, SeriesNode node, String prefix) {
        sender.sendMessage(prefix + node.getSeries() + " - " + node.getBracket().getNodes().indexOf(node));
        for (SeriesNode childNode : node.getChildren()) {
            this.printNode(sender, childNode, prefix + "  ");
        }
    }

}
