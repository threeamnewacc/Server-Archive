package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class KillTopCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) {
				sender.sendMessage(ChatColor.RED + "The UHC hasn't started yet.");
				return true;
			}

			List<String> topKills = new LinkedList<>();
			List<Integer> topKillCounts = new LinkedList<>();

			for (UHCPlayer uhcPlayer : UHCPlayerManager.getAllUHCPlayers()) {
				if (uhcPlayer.getKills() != 0) {
					// Insert automatically if top kills list is empty
					topKills.add(uhcPlayer.getDisguisedName());
					topKillCounts.add(uhcPlayer.getKills());

					for (int i = 0; i < topKillCounts.size(); i++) {
						// Only worry about top 10 kills
						if (i == 10) break;

						// We have more kills than someone in the top 10, shove them further down
						if (uhcPlayer.getKills() > topKillCounts.get(i)) {
							topKills.add(i, uhcPlayer.getDisguisedName());
							topKillCounts.add(i, uhcPlayer.getKills());
							break;
						}
					}
				}
			}

			sender.sendMessage(ChatColor.GOLD + "[Top 10 Kills]");
			if (!topKills.isEmpty()) {
				for (int i = 0; i < topKills.size(); i++) {
					// Only show 10
					if (i == 10) break;

					sender.sendMessage(ChatColor.GREEN + topKills.get(i) + ChatColor.YELLOW + " - " + topKillCounts.get(i));
				}
			} else {
				sender.sendMessage(ChatColor.GREEN + "None");
			}
		}
		return true;
	}

}
