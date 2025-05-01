package net.badlion.uhc.commands.handlers.teams;

import net.badlion.smellychat.managers.ChatSettingsManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.util.MessageHelperUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InviteCommandHandler {

    public static void handleInviteCommand(Player player, String[] args) {
        InviteCommandHandler.handleInviteCommand(player, args, true);
    }

    public static void handleInviteCommand(Player player, String[] args, boolean verbose) {
        if (args.length == 2) {
            UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(player.getUniqueId());
            if (uhcp.getTeam().getLeader().equals(player.getUniqueId())) {
                if (uhcp.getTeam().getSize() < (int) BadlionUHC.getInstance().getConfigurator().getOption(BadlionUHC.CONFIG_OPTIONS.TEAMSIZE.name()).getValue()) {
	                // Make sure they're not trying to invite themselves
                    if (!args[1].equalsIgnoreCase(player.getDisguisedName())) {
                        Player invited = Bukkit.getPlayerExact(args[1]);
	                    boolean invitingDisguisedName = invited != null && invited.isDisguised() && invited.getDisguisedName().equalsIgnoreCase(args[1]);
                        if (invited != null && (!invited.isDisguised() || invitingDisguisedName)) {
                            if (invited.hasPermission("badlion.famous") && !player.hasPermission("badlion.famous")) {
                                player.sendMessage(ChatColor.RED + "You cannot invite famous players.");
                                return;
                            }

	                        // Is this player ignoring the inviter?
	                        if (ChatSettingsManager.getChatSettings(invited).isIgnoring(player)) {
		                        player.sendMessage(ChatColor.RED + "This player has you on their ignore list, you cannot invite them to your team.");
		                        return;
	                        }

                            UHCPlayer uhcpInvited = UHCPlayerManager.getUHCPlayer(invited.getUniqueId());
                            if (uhcpInvited != null) {
                                if (uhcpInvited.getState() != UHCPlayer.State.PLAYER && uhcpInvited.getState() != UHCPlayer.State.SPEC_IN_GAME) {
                                    player.sendMessage(ChatColor.RED + "Cannot invite this player.");
                                    return;

                                }

                                if (uhcpInvited.getTeam().getSize() > 1) {
                                    player.sendMessage(ChatColor.RED + "This player is already in a team.");
                                    return;
                                }

                                if (uhcpInvited.getTeamRequest() == null) {
                                    uhcpInvited.setTeamRequest(uhcp.getTeam());

                                    for (UUID pl : uhcp.getTeam().getUuids()) {
                                        // Skip the person who invited the player to the team
                                        if (pl.equals(player.getUniqueId())) {
                                            continue;
                                        }

                                        MessageHelperUtil.messagePlayerIfOnline(pl, ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has invited " + ChatColor.YELLOW + invited.getDisguisedName() + ChatColor.AQUA + " to the team!");
                                    }

                                    if (verbose) {
                                        player.sendMessage(ChatColor.AQUA + "You have invited " + ChatColor.YELLOW + invited.getDisguisedName() + ChatColor.AQUA + " to the team!");
                                        invited.sendMessage(ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has invited you to join their team!");
                                        invited.sendMessage(ChatColor.AQUA + "Do \"/team accept\" or \"/team deny\" to accept/deny their request.");
                                    }

                                    // Cancel task
                                    InviteCommandHandler.cancelPendingInvite(player, invited, uhcpInvited);
                                } else {
                                    player.sendMessage(ChatColor.RED + "That player already has a pending invite.");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You cannot invite that player/player not found.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Please enter a valid player name.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You cannot invite yourself!");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You cannot invite anymore players to your team!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "You must be the leader of your team to invite people.");
            }
        } else {
            player.sendMessage("Usage: /team invite <player>");
        }
    }

    private static void cancelPendingInvite(final Player inviter, final Player invited, final UHCPlayer uhcpinvited) {
        Bukkit.getScheduler().runTaskLater(BadlionUHC.getInstance(), new Runnable() {
            public void run() {
                if (uhcpinvited.getTeamRequest() != null) { // Player hasn't already accepted/denied the request
                    uhcpinvited.setTeamRequest(null);
                    inviter.sendMessage(ChatColor.YELLOW + invited.getDisguisedName() + " has failed to respond to your invitation request.");
                    invited.sendMessage(ChatColor.YELLOW + inviter.getDisguisedName() + "'s team invitation has expired.");
                }
            }
        }, 200L);
    }

}
