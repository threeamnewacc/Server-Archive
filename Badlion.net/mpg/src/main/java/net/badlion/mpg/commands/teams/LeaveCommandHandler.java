package net.badlion.mpg.commands.teams;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.bukkitevents.teams.TeamLeaveEvent;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LeaveCommandHandler {

    public static void handleLeaveCommand(Player player) {
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());
        if (mpgPlayer.getTeam().getTeamSize() == 1) {
            player.sendMessage(ChatColor.RED + "You cannot leave the team when you are the only one left.");
        } else {
            TeamLeaveEvent event = new TeamLeaveEvent(mpgPlayer);
            MPG.getInstance().getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            MPGTeam team = mpgPlayer.getTeam();
            team.remove(mpgPlayer);

	        // Only give them a new team if this is an FFA
	        if (MPG.GAME_TYPE == MPG.GameType.FFA) {
		        mpgPlayer.setTeam(new MPGTeam(mpgPlayer.getUsername()));
	        }

            team.sendMessage(ChatColor.YELLOW + player.getDisguisedName() + " has left the team!");
            player.sendMessage(ChatColor.YELLOW + "You have left the team!");
        }
    }

}
