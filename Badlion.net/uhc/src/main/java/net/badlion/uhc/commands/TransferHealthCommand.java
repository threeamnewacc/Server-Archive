package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TransferHealthCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		/*if (BadlionUHC.getInstance().getTeamHealthShare() != null && BadlionUHC.getInstance().getTeamHealthShare()) {
			sender.sendMessage(ChatColor.RED + "Cannot use this feature with team health sharing enabled.");
			return true;
		}

		if (sender instanceof Player) {
			if (args.length == 2) {
				try {
					Player player = (Player) sender;
					UHCPlayer uhcPlayer = BadlionUHC.getInstance().getPlayers().get(player.getName());

					int amountTransfering = Integer.valueOf(args[1]) * 2;
					if (amountTransfering < 2 || amountTransfering > 18) {
						player.sendMessage(ChatColor.RED + "You cannot transfer more than 9 hearts or less than 1 heart.");
						return true;
					}

					if (amountTransfering > player.getHealth() - 2) {
						player.sendMessage(ChatColor.RED + "You do not enough health!");
						return true;
					}

					if (uhcPlayer == null || uhcPlayer.getTeam() == null || BadlionUHC.getInstance().getTeamType().equals(UHCTeam.TeamType.SOLO)) {
						player.sendMessage(ChatColor.RED + "You are not in a team!");
						return true;
					}

					if (args[0].equalsIgnoreCase(player.getName())) {
						player.sendMessage(ChatColor.RED + "You cannot transfer health to yourself!");
					}

					UHCTeam team = uhcPlayer.getTeam();

					// Try to find the teammate receiving health
					Player receiving = null;
					if (args[0].equalsIgnoreCase(team.getLeader().getName())) {
						receiving = team.getLeader();
					} else {
						for (Player p : team.getPlayers()) {
							if (args[0].equalsIgnoreCase(p.getName())) {
								receiving = p;
								break;
							}
						}
					}

					// Found player?
					if (receiving == null) {
						player.sendMessage(ChatColor.RED + "That player is not in your team!");
						return true;
					}

					// Player online?
					if (!receiving.isOnline()) {
						player.sendMessage(ChatColor.RED + "That player is offline!");
						return true;
					}

					// Transfer health
					int receivingHealth = (int) receiving.getHealth();
					if (receivingHealth == 20) {
						player.sendMessage(ChatColor.RED + "Teammate " + receiving.getName() + " is already at full health");
					} else if (receiving.getHealth() + amountTransfering > 20) { // Too much being transfered, send as much as we can
						int canReceive = 20 - receivingHealth;

						player.setHealth(player.getHealth() - canReceive);
						receiving.setHealth(receiving.getHealth() + canReceive);

						player.sendMessage(ChatColor.GREEN + "Gave " + (canReceive / 2) + " hearts to " + receiving.getName());
						receiving.sendMessage(ChatColor.GREEN + "Teammate " + player.getName() + " has gave you " + (canReceive / 2) + " hearts");
					} else {
						player.setHealth(player.getHealth() - amountTransfering);
						receiving.setHealth(receiving.getHealth() + amountTransfering);

						player.sendMessage(ChatColor.GREEN + "Gave " + (amountTransfering / 2) + " hearts to " + receiving.getName());
						receiving.sendMessage(ChatColor.GREEN + "Teammate " + player.getName() + " has gave you " + (amountTransfering / 2) + " hearts");

					}

					return true;
				} catch (NumberFormatException e) {

				}
			}
		} */
		return false;
	}

}
