package net.badlion.mpg.commands.teams;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.bukkitevents.teams.TeamKickEvent;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KickCommandHandler {

	public static void handleKickCommand(Player player, String[] args) {
		if (args.length == 2) {
			MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());

			// Are we the leader?
			if (mpgPlayer.getTeam().getLeader().equals(player.getUniqueId())) {
				if (!args[1].equalsIgnoreCase(player.getDisguisedName())) {
					Player kickedPlayer = MPG.getInstance().getServer().getPlayer(args[1]);
					if (kickedPlayer != null) {
						MPGPlayer mpgKicked = MPGPlayerManager.getMPGPlayer(kickedPlayer.getUniqueId());
						if (mpgKicked.getTeam() == mpgPlayer.getTeam()) {
							TeamKickEvent event = new TeamKickEvent(mpgPlayer, mpgKicked);
							MPG.getInstance().getServer().getPluginManager().callEvent(event);

							if (event.isCancelled()) {
								return;
							}

							MPGTeam team = mpgPlayer.getTeam();
							team.remove(mpgKicked);

							// Only give them a new team if this is an FFA
							if (MPG.GAME_TYPE == MPG.GameType.FFA) {
								mpgPlayer.setTeam(new MPGTeam(mpgPlayer.getUsername()));
							}

							mpgPlayer.getTeam().sendMessage(ChatColor.YELLOW + kickedPlayer.getDisguisedName() + " from the team!");

							kickedPlayer.sendMessage(ChatColor.YELLOW + player.getDisguisedName() + " has kicked you from their team!");
						} else {
							player.sendMessage(ChatColor.RED + "That player is not in your team!");
						}
					} else {
						player.sendMessage(ChatColor.RED + "This player does not exist or is not on your team.");
					}
				} else {
					player.sendMessage(ChatColor.RED + "You cannot kick yourself!");
				}
			} else {
				player.sendMessage(ChatColor.RED + "Only the team leader can kick people!");
			}
		} else {
			player.sendMessage("Usage: /team kick <player>");
		}
	}

}