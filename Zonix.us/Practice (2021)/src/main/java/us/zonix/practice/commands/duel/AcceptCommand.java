package us.zonix.practice.commands.duel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.managers.PartyManager;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchRequest;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.queue.QueueType;
import us.zonix.practice.util.StringUtil;

public class AcceptCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public AcceptCommand() {
        super("accept");
        this.setDescription("Accept a player's duel.");
        this.setUsage(ChatColor.RED + "Usage: /accept <player>");
    }

    public boolean execute(CommandSender sender, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        } else {
            Player player = (Player)sender;
            if (args.length < 1) {
                player.sendMessage(this.usageMessage);
                return true;
            } else {
                PlayerData playerData = this.plugin.getPlayerManager().getPlayerData(player.getUniqueId());
                if (playerData.getPlayerState() != PlayerState.SPAWN) {
                    player.sendMessage(ChatColor.RED + "Unable to accept a duel within your duel.");
                    return true;
                } else {
                    Player target = this.plugin.getServer().getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
                        return true;
                    } else if (player.getName().equals(target.getName())) {
                        player.sendMessage(ChatColor.RED + "You can't duel yourself.");
                        return true;
                    } else {
                        PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
                        if (targetData.getPlayerState() != PlayerState.SPAWN) {
                            player.sendMessage(ChatColor.RED + "That player is currently busy.");
                            return true;
                        } else {
                            MatchRequest request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId());
                            if (args.length > 1) {
                                Kit kit = this.plugin.getKitManager().getKit(args[1]);
                                if (kit != null) {
                                    request = this.plugin.getMatchManager().getMatchRequest(target.getUniqueId(), player.getUniqueId(), kit.getName());
                                }
                            }

                            if (request == null) {
                                player.sendMessage(ChatColor.RED + "You do not have any pending requests.");
                                return true;
                            } else {
                                if (request.getRequester().equals(target.getUniqueId())) {
                                    List<UUID> playersA = new ArrayList<>();
                                    List<UUID> playersB = new ArrayList<>();
                                    PartyManager partyManager = this.plugin.getPartyManager();
                                    Party party = partyManager.getParty(player.getUniqueId());
                                    Party targetParty = partyManager.getParty(target.getUniqueId());
                                    if (request.isParty()) {
                                        if (party == null
                                            || targetParty == null
                                            || !partyManager.isLeader(target.getUniqueId())
                                            || !partyManager.isLeader(target.getUniqueId())) {
                                            player.sendMessage(ChatColor.RED + "That player is not a party leader.");
                                            return true;
                                        }

                                        playersA.addAll(party.getMembers());
                                        playersB.addAll(targetParty.getMembers());
                                    } else {
                                        if (party != null || targetParty != null) {
                                            player.sendMessage(ChatColor.RED + "That player is already in a party.");
                                            return true;
                                        }

                                        playersA.add(player.getUniqueId());
                                        playersB.add(target.getUniqueId());
                                    }

                                    Kit kit = this.plugin.getKitManager().getKit(request.getKitName());
                                    MatchTeam teamA = new MatchTeam(target.getUniqueId(), playersB, 0);
                                    MatchTeam teamB = new MatchTeam(player.getUniqueId(), playersA, 1);
                                    Match match = new Match(request.getArena(), kit, QueueType.UNRANKED, request.isBestOfThree(), teamA, teamB);
                                    Player leaderA = this.plugin.getServer().getPlayer(teamA.getLeader());
                                    Player leaderB = this.plugin.getServer().getPlayer(teamB.getLeader());
                                    String teamMatch = match.isPartyMatch() ? "'s Party" : "";
                                    match.broadcast(
                                        ChatColor.YELLOW
                                            + "Starting duel. "
                                            + ChatColor.GREEN
                                            + "("
                                            + leaderA.getName()
                                            + teamMatch
                                            + " vs "
                                            + leaderB.getName()
                                            + teamMatch
                                            + ") in "
                                            + match.getArena().getName()
                                    );
                                    this.plugin.getMatchManager().createMatch(match);
                                }

                                return true;
                            }
                        }
                    }
                }
            }
        }
    }
}
