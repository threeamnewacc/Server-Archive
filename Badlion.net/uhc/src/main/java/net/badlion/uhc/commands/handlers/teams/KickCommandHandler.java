package net.badlion.uhc.commands.handlers.teams;

import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.managers.UHCPlayerManager;
import net.badlion.uhc.util.MessageHelperUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickCommandHandler {

	public static void handleKickCommand(Player player, String[] args) {
		if (args.length == 2) {
			UHCPlayer uhcp = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

			// Are we the leader?
			if (uhcp.getTeam().getLeader().equals(player.getUniqueId())) {
				// Make sure they're not kicking themselves
				if (!args[1].equalsIgnoreCase(player.getDisguisedName())) {
					Player kicked = Bukkit.getPlayerExact(args[1]);
					boolean kickingDisguisedName = kicked != null && kicked.isDisguised() && kicked.getDisguisedName().equalsIgnoreCase(args[1]);
					if (kicked != null && (!kicked.isDisguised() || kickingDisguisedName)) {
						UHCPlayer uhcpKicked = UHCPlayerManager.getUHCPlayer(kicked.getUniqueId());
						if (uhcpKicked.getTeam() == uhcp.getTeam()) {
							UHCTeam team = uhcp.getTeam();
							team.removePlayer(kicked.getUniqueId());
							uhcpKicked.setTeam(new UHCTeam(kicked.getUniqueId()));

							for (UUID uuid : team.getUuids()) {
								MessageHelperUtil.messagePlayerIfOnline(uuid, ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " kicked " + ChatColor.YELLOW + kicked.getDisguisedName() + ChatColor.AQUA + " from the team!");
							}

							kicked.sendMessage(ChatColor.YELLOW + player.getDisguisedName() + ChatColor.AQUA + " has kicked you from their team!");
						} else {
							player.sendMessage(ChatColor.RED + "That player is not in your team!");
						}
					} else {
						player.sendMessage(ChatColor.RED + "That player is offline or is not on your team.");
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