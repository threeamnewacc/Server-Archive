// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.commands;

import club.minemen.core.util.finalutil.StringUtil;
import club.minemen.practice.match.Match;
import java.util.Iterator;
import club.minemen.practice.player.PlayerData;
import club.minemen.practice.tournament.Tournament;
import club.minemen.practice.kit.Kit;
import club.minemen.practice.team.KillableTeam;
import club.minemen.practice.util.TeamUtil;
import club.minemen.practice.match.MatchTeam;
import java.util.UUID;
import club.minemen.practice.tournament.TournamentState;
import club.minemen.practice.player.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.function.Consumer;
import club.minemen.core.clickable.Clickable;
import club.minemen.core.util.finalutil.PlayerUtil;
import club.minemen.core.rank.Rank;
import org.bukkit.command.CommandSender;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import org.bukkit.command.Command;

public class TournamentCommand extends Command
{
    private static final String HELP_MESSAGE;
    private final Practice plugin;
    
    public TournamentCommand() {
        super("tournament");
        this.plugin = Practice.getInstance();
        this.setUsage(CC.RED + "Usage: /tournament [args]");
    }
    
    public boolean execute(final CommandSender commandSender, final String s, final String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(TournamentCommand.HELP_MESSAGE);
            return true;
        }
        final String lowerCase = args[0].toLowerCase();
        switch (lowerCase) {
            case "create": {
                if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
                    return true;
                }
                if (args.length == 5) {
                    try {
                        final int id = Integer.parseInt(args[1]);
                        final int teamSize = Integer.parseInt(args[3]);
                        final int size = Integer.parseInt(args[4]);
                        final String kitName = args[2];
                        if (size % teamSize != 0) {
                            commandSender.sendMessage(CC.RED + "This tournament size and team size would not work together.");
                            return true;
                        }
                        if (this.plugin.getTournamentManager().getTournament(id) != null) {
                            commandSender.sendMessage(CC.RED + "This tournament already exists.");
                            return true;
                        }
                        final Kit kit = this.plugin.getKitManager().getKit(kitName);
                        if (kit == null) {
                            commandSender.sendMessage(CC.RED + "This kit does not exist!");
                            return true;
                        }
                        this.plugin.getTournamentManager().createTournament(commandSender, id, teamSize, size, kitName);
                    }
                    catch (final NumberFormatException e) {
                        commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
                    }
                    break;
                }
                commandSender.sendMessage(CC.RED + "Usage: /tournament create <id> <kit> <team size> <tournament size>");
                break;
            }
            case "remove": {
                if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
                    return true;
                }
                if (args.length == 2) {
                    final int id = Integer.parseInt(args[1]);
                    final Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                    if (tournament != null) {
                        this.plugin.getTournamentManager().removeTournament(id);
                        commandSender.sendMessage(CC.PRIMARY + "Successfully removed tournament " + CC.SECONDARY + id + CC.PRIMARY + ".");
                    }
                    else {
                        commandSender.sendMessage(CC.RED + "This tournament does not exist.");
                    }
                    break;
                }
                commandSender.sendMessage(CC.RED + "Usage: /tournament remove <id>");
                break;
            }
            case "announce": {
                if (!PlayerUtil.testPermission(commandSender, Rank.DEVELOPER)) {
                    return true;
                }
                if (args.length == 2) {
                    final int id = Integer.parseInt(args[1]);
                    final Tournament tournament = this.plugin.getTournamentManager().getTournament(id);
                    if (tournament != null) {
                        final Clickable clickable = new Clickable(CC.SECONDARY + commandSender.getName() + CC.PRIMARY + " is hosting a " + CC.SECONDARY + tournament.getTeamSize() + "v" + tournament.getTeamSize() + " " + tournament.getKitName() + CC.PRIMARY + " tournament! " + CC.GREEN + "[Join]", CC.GREEN + "Click to join!", "/tournament join " + id);
                        this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)this.plugin, () -> this.plugin.getServer().getOnlinePlayers().forEach(clickable::sendToPlayer));
                    }
                    break;
                }
                commandSender.sendMessage(CC.RED + "Usage: /tournament announce <id>");
                break;
            }
            case "join": {
                if (!(commandSender instanceof Player)) {
                    return true;
                }
                final Player player = (Player)commandSender;
                final PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(CC.RED + "You can't do that in this state.");
                    return true;
                }
                if (this.plugin.getTournamentManager().isInTournament(player.getUniqueId())) {
                    player.sendMessage(CC.RED + "You are already in a tournament!");
                    return true;
                }
                if (args.length == 2) {
                    try {
                        final int id2 = Integer.parseInt(args[1]);
                        final Tournament tournament2 = this.plugin.getTournamentManager().getTournament(id2);
                        if (tournament2 != null) {
                            if (tournament2.getSize() > tournament2.getPlayers().size()) {
                                if ((tournament2.getTournamentState() == TournamentState.WAITING || tournament2.getTournamentState() == TournamentState.STARTING) && tournament2.getCurrentRound() == 1) {
                                    this.plugin.getTournamentManager().joinTournament(id2, player);
                                }
                                else {
                                    player.sendMessage(CC.RED + "This tournament has already started!");
                                }
                            }
                            else {
                                player.sendMessage(CC.RED + "This tournament is already full!");
                            }
                        }
                        else {
                            player.sendMessage(CC.RED + "This tournament doesn't exist!");
                        }
                    }
                    catch (final NumberFormatException e2) {
                        player.sendMessage(CC.RED + "This is not a number!");
                    }
                    break;
                }
                player.sendMessage(CC.RED + "Usage: /tournament join <id>");
                break;
            }
            case "status": {
                if (args.length == 2) {
                    try {
                        final int id2 = Integer.parseInt(args[1]);
                        final Tournament tournament2 = this.plugin.getTournamentManager().getTournament(id2);
                        if (tournament2 != null) {
                            final StringBuilder builder = new StringBuilder();
                            builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
                            builder.append(CC.SECONDARY).append("Tournament ").append(tournament2.getId()).append(CC.PRIMARY).append("'s matches:");
                            builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
                            for (final UUID matchUUID : tournament2.getMatches()) {
                                final Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);
                                final MatchTeam teamA = match.getTeams().get(0);
                                final MatchTeam teamB = match.getTeams().get(1);
                                final String teamANames = TeamUtil.getNames(teamA);
                                final String teamBNames = TeamUtil.getNames(teamB);
                                builder.append(teamANames).append(" vs. ").append(teamBNames).append("\n");
                            }
                            builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
                            builder.append(CC.PRIMARY).append("Round: ").append(CC.SECONDARY).append(tournament2.getCurrentRound()).append("\n");
                            builder.append(CC.PRIMARY).append("Players: ").append(CC.SECONDARY).append(tournament2.getPlayers().size()).append("\n");
                            builder.append(CC.RED).append(" ").append(CC.RED).append("\n");
                            commandSender.sendMessage(builder.toString());
                        }
                        else {
                            commandSender.sendMessage(CC.RED + "This tournament does not exist!");
                        }
                    }
                    catch (final NumberFormatException e2) {
                        commandSender.sendMessage(CC.RED + "This is not a number!");
                    }
                    break;
                }
                break;
            }
            default: {
                commandSender.sendMessage(TournamentCommand.HELP_MESSAGE);
                break;
            }
        }
        return false;
    }
    
    static {
        HELP_MESSAGE = StringUtil.center(CC.PRIMARY + "[==" + CC.SECONDARY + "Tournament Commands" + CC.PRIMARY + "==]\n") + StringUtil.center(CC.SECONDARY + "/tournament join <id>" + CC.PRIMARY + " - Joins a tournament\n") + StringUtil.center(CC.SECONDARY + "/tournament status <id>" + CC.PRIMARY + " - Gives you a status");
    }
}
