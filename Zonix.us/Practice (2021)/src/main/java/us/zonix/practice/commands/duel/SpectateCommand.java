package us.zonix.practice.commands.duel;

import java.util.Arrays;
import me.maiko.dexter.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.practice.Practice;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.events.oitc.OITCEvent;
import us.zonix.practice.events.parkour.ParkourEvent;
import us.zonix.practice.events.redrover.RedroverEvent;
import us.zonix.practice.events.sumo.SumoEvent;
import us.zonix.practice.match.Match;
import us.zonix.practice.match.MatchTeam;
import us.zonix.practice.party.Party;
import us.zonix.practice.player.PlayerData;
import us.zonix.practice.player.PlayerState;
import us.zonix.practice.util.StringUtil;

public class SpectateCommand extends Command {
    private final Practice plugin = Practice.getInstance();

    public SpectateCommand() {
        super("spectate");
        this.setDescription("Spectate a player's match.");
        this.setUsage(ChatColor.RED + "Usage: /spectate <player>");
        this.setAliases(Arrays.asList("spec"));
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
                Party party = this.plugin.getPartyManager().getParty(playerData.getUniqueId());
                if (party == null && (playerData.getPlayerState() == PlayerState.SPAWN || playerData.getPlayerState() == PlayerState.SPECTATING)) {
                    Player target = this.plugin.getServer().getPlayer(args[0]);
                    if (target == null) {
                        player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
                        return true;
                    } else {
                        PlayerData targetData = this.plugin.getPlayerManager().getPlayerData(target.getUniqueId());
                        if (targetData.getPlayerState() == PlayerState.EVENT) {
                            PracticeEvent event = this.plugin.getEventManager().getEventPlaying(target);
                            if (event == null) {
                                player.sendMessage(ChatColor.RED + "That player is currently not in an event.");
                                return true;
                            } else {
                                if (event instanceof SumoEvent) {
                                    player.performCommand("eventspectate Sumo");
                                } else if (event instanceof OITCEvent) {
                                    player.performCommand("eventspectate OITC");
                                } else if (event instanceof ParkourEvent) {
                                    player.performCommand("eventspectate Parkour");
                                } else if (event instanceof RedroverEvent) {
                                    player.performCommand("eventspectate Redrover");
                                }

                                return true;
                            }
                        } else if (targetData.getPlayerState() != PlayerState.FIGHTING) {
                            player.sendMessage(ChatColor.RED + "That player is currently not in a fight.");
                            return true;
                        } else {
                            Match targetMatch = this.plugin.getMatchManager().getMatch(targetData);
                            if (!targetMatch.isParty()) {
                                if (!targetData.getOptions().isSpectators() && !player.hasPermission("core.staff")) {
                                    player.sendMessage(ChatColor.RED + "That player has ignored spectators.");
                                    return true;
                                }

                                MatchTeam team = targetMatch.getTeams().get(0);
                                MatchTeam team2 = targetMatch.getTeams().get(1);
                                PlayerData otherPlayerData = this.plugin
                                    .getPlayerManager()
                                    .getPlayerData(team.getPlayers().get(0) == target.getUniqueId() ? team2.getPlayers().get(0) : team.getPlayers().get(0));
                                if (otherPlayerData != null && !otherPlayerData.getOptions().isSpectators() && !player.hasPermission("core.staff")) {
                                    player.sendMessage(ChatColor.RED + "That player has ignored spectators.");
                                    return true;
                                }
                            }

                            if (playerData.getPlayerState() == PlayerState.SPECTATING) {
                                Match match = this.plugin.getMatchManager().getSpectatingMatch(player.getUniqueId());
                                if (match.equals(targetMatch)) {
                                    player.sendMessage(ChatColor.RED + "You are already spectating this player.");
                                    return true;
                                }

                                match.removeSpectator(player.getUniqueId());
                            }

                            String targetName = Profile.getByUuidIfAvailable(target.getUniqueId()).getRank().getGameColor() + target.getName();
                            player.sendMessage(ChatColor.YELLOW + "You are now spectating " + ChatColor.GREEN + targetName + ChatColor.YELLOW + ".");
                            this.plugin.getMatchManager().addSpectator(player, playerData, target, targetMatch);
                            return true;
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Cannot execute this command in your current state.");
                    return true;
                }
            }
        }
    }
}
