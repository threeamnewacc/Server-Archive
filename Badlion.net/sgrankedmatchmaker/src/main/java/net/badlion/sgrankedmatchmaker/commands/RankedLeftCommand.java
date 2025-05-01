package net.badlion.sgrankedmatchmaker.commands;

import net.badlion.sgrankedmatchmaker.managers.RankedLeftManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankedLeftCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			if (!player.hasPermission("badlion.donator")) {
				int rankedLeft = RankedLeftManager.DEFAULT_MAX_NUM_OF_RANKED_MATCHES_PER_DAY - RankedLeftManager.getNumberOfRankedMatchesToday(player.getUniqueId());
				if (rankedLeft > 1) {
					player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.RED + rankedLeft + ChatColor.GREEN + " SG ranked matches left!");
				} else if (rankedLeft == 0) {
					player.sendMessage(ChatColor.GREEN + "You have " + ChatColor.RED + rankedLeft + ChatColor.GREEN + " SG ranked match left!");
				} else {
					player.sendMessage(ChatColor.GREEN + "You have run out of SG ranked matches left! Donate at http://www.badlion.net/ or vote for more!");
				}
			} else {
				player.sendMessage(ChatColor.GREEN + "You have unlimited ranked matches!");
			}
		}
		return true;
	}

}
