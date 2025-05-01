package net.badlion.smellymapvotes.commands;

import net.badlion.smellymapvotes.VoteManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MapVoteCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if (sender instanceof Player) {
			if (strings.length == 2 && sender.isOp()) {

				Player player = Bukkit.getPlayerExact(strings[0]);
				if (player != null) {
					sender.sendMessage("k");
					VoteManager.sendVoteMessage(player, strings[1]);
				} else {
					sender.sendMessage("wot?");
				}
				return true;
			}

			if (strings.length == 1) {
				if (VoteManager.canVote((Player) sender)) {
					try {
						int points = Integer.valueOf(strings[0]);

						if (points < 1 || points > 5) return false;

						VoteManager.addVote(((Player) sender).getUniqueId().toString(), points);

						sender.sendMessage(ChatColor.GREEN + "Thank you for voting!");
						return true;
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				} else {
					sender.sendMessage(ChatColor.YELLOW + "You have already voted!");
				}
			}
		}
		return true;
	}

}
