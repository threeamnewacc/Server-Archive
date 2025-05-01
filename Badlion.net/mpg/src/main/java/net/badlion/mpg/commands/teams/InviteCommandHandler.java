package net.badlion.mpg.commands.teams;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.teams.TeamInviteEvent;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class InviteCommandHandler {

    public static void handleInviteCommand(Player player, String[] args) {
        if (args.length == 2) {
            MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
            if (mpgPlayer.getTeam().getLeader().equals(player.getUniqueId())) {
                if (!mpgPlayer.getTeam().isFull()) {
                    if (!args[1].equalsIgnoreCase(player.getDisguisedName())) {
                        Player invited = Bukkit.getPlayerExact(args[1]);
                        if (invited != null) {
                            if (invited.hasPermission("badlion.famous") && !player.hasPermission("badlion.famous")) {
                                player.sendMessage(ChatColor.RED + "You cannot invite famous players.");
                                return;
                            }

                            MPGPlayer mpgPlayerInvited = MPGPlayerManager.getMPGPlayer(invited.getUniqueId());
                            if (mpgPlayerInvited != null) {
                                if (mpgPlayerInvited.getTeam().getTeamSize() > 1) {
                                    player.sendMessage(ChatColor.RED + "This player is already in a team.");
                                    return;
                                }

                                if (mpgPlayerInvited.getInvitedTeam() == null) {
                                    TeamInviteEvent event = new TeamInviteEvent(mpgPlayerInvited, mpgPlayer.getTeam());
                                    MPG.getInstance().getServer().getPluginManager().callEvent(event);

                                    if (event.isCancelled()) {
                                        return;
                                    }

                                    mpgPlayerInvited.setInvitedTeam(mpgPlayer.getTeam());

                                    mpgPlayer.getTeam().sendMessage(ChatColor.YELLOW + player.getDisguisedName() + " has invited " + invited.getDisguisedName() + " to the team!");

                                    player.sendMessage(ChatColor.YELLOW + "You have invited " + invited.getDisguisedName() + " to the team!");
                                    invited.sendMessage(ChatColor.YELLOW + player.getDisguisedName() + " has invited you to join their team!");
                                    invited.sendMessage(ChatColor.YELLOW + "Do \"/team accept\" or \"/team deny\" to accept/deny their request.");

                                    // Cancel task
                                    InviteCommandHandler.cancelPendingInvite(player, invited, mpgPlayerInvited);
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

    private static void cancelPendingInvite(final Player inviter, final Player invited, final MPGPlayer mpgPlayerInvited) {
        Bukkit.getScheduler().runTaskLater(MPG.getInstance(), new Runnable() {
            public void run() {
                if (mpgPlayerInvited.getInvitedTeam() != null) { // Player hasn't already accepted/denied the request
                    mpgPlayerInvited.setInvitedTeam(null);
                    inviter.sendMessage(ChatColor.YELLOW + invited.getDisguisedName() + " has failed to respond to your invitation request.");
                    invited.sendMessage(ChatColor.YELLOW + inviter.getDisguisedName() + "'s team invitation has expired.");
                }
            }
        }, 200L);
    }

}
