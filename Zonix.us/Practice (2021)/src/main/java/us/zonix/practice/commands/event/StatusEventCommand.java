package us.zonix.practice.commands.event;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.tournament.Tournament;
import us.zonix.practice.util.Clickable;

public class StatusEventCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public StatusEventCommand() {
        super("eventstatus");
        this.setDescription("Show an event or tournament status.");
        this.setUsage(ChatColor.RED + "Usage: /status");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
            if (playerData.getPlayerState() != PlayerState.SPAWN) {
                player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                return true;
            } else if (this.plugin.getTournamentManager().getTournaments().size() == 0) {
                player.sendMessage(ChatColor.RED + "There is no available tournaments.");
                return true;
            } else {
                for (Tournament tournament : this.plugin.getTournamentManager().getTournaments().values()) {
                    if (tournament == null) {
                        player.sendMessage(ChatColor.RED + "This tournament doesn't exist.");
                        return true;
                    }

                    player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                    player.sendMessage(" ");
                    player.sendMessage(
                        ChatColor.RED.toString()
                            + "Tournament ["
                            + tournament.getTeamSize()
                            + "v"
                            + tournament.getTeamSize()
                            + "] "
                            + ChatColor.WHITE.toString()
                            + tournament.getKitName()
                    );
                    if (tournament.getMatches().size() == 0) {
                        player.sendMessage(ChatColor.RED + "There is no available matches.");
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                        return true;
                    }

                    for (UUID matchUUID : tournament.getMatches()) {
                        Match match = this.plugin.getMatchManager().getMatchFromUUID(matchUUID);
                        MatchTeam teamA = match.getTeams().get(0);
                        MatchTeam teamB = match.getTeams().get(1);
                        String teamANames = tournament.getTeamSize() > 1 ? teamA.getLeaderName() + "'s Party" : teamA.getLeaderName();
                        String teamBNames = tournament.getTeamSize() > 1 ? teamB.getLeaderName() + "'s Party" : teamB.getLeaderName();
                        Clickable clickable = new Clickable(
                            ChatColor.WHITE.toString()
                                + ChatColor.BOLD
                                + "* "
                                + ChatColor.GOLD.toString()
                                + teamANames
                                + " vs "
                                + teamBNames
                                + ChatColor.DARK_GRAY
                                + " ┃ "
                                + ChatColor.GRAY
                                + "[Click to Spectate]",
                            ChatColor.GRAY + "Click to spectate",
                            "/spectate " + teamA.getLeaderName()
                        );
                        clickable.sendToPlayer(player);
                    }

                    player.sendMessage(" ");
                    player.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + "----------------------------------------------------");
                }

                return true;
            }
        }
    }
}
