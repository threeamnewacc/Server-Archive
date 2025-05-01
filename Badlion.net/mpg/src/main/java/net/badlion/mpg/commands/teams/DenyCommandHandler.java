package net.badlion.mpg.commands.teams;

import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DenyCommandHandler {

    public static void handleDenyCommand(Player player) {
        // Don't check to see if they're already in a team, InviteCommand handles that
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
        if (mpgPlayer.getInvitedTeam() != null) {
	        // Message rest of team members
            mpgPlayer.getInvitedTeam().sendMessage(ChatColor.YELLOW + player.getDisguisedName() + " has denied the team invitation!");
            mpgPlayer.setInvitedTeam(null);

            player.sendMessage(ChatColor.YELLOW + "You have denied the team invitation!");
        } else {
            player.sendMessage(ChatColor.RED + "You do not have a pending team invitation!");
        }
    }

}
