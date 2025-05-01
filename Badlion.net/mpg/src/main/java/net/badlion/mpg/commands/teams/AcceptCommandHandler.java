package net.badlion.mpg.commands.teams;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.teams.TeamAcceptEvent;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.managers.MPGTeamManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AcceptCommandHandler {

    public static void handleAcceptCommand(Player player) {
        // Don't check to see if they're already in a team, InviteCommand handles that
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
        if (mpgPlayer.getInvitedTeam() != null) {
	        // Plus 1 to players size because of the leader
            if (mpgPlayer.getInvitedTeam().isFull()) {
                mpgPlayer.setInvitedTeam(null);
                player.sendMessage(ChatColor.RED + "That team is already full!");
                return;
            }

            TeamAcceptEvent event = new TeamAcceptEvent(mpgPlayer);
            MPG.getInstance().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            MPGTeamManager.removeTeam(mpgPlayer.getTeam());
            mpgPlayer.setTeam(mpgPlayer.getInvitedTeam());
	        mpgPlayer.setInvitedTeam(null);

            mpgPlayer.getTeam().sendMessage(ChatColor.YELLOW + player.getDisguisedName() + " has joined the team!");

            mpgPlayer.getTeam().add(mpgPlayer);
            player.sendMessage(ChatColor.YELLOW + "You have joined the team!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a pending team invitation!");
        }
    }

}
