package net.badlion.score.commands;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScoreCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length == 0) {
				Faction faction = FPlayers.i.get(player).getFaction();
				if (faction != null) {
					// TODO: SEND PLAYER THEIR CONTRIBUTION TO FACTION'S SCORE
				} else {
					player.sendMessage(ChatColor.RED + "You're not in a faction!");
				}
				return true;
			} else if (args.length == 1) {
				if (args[1].equalsIgnoreCase("faction")) {
					Faction faction = FPlayers.i.get(player).getFaction();
					if (faction != null) {
						// TODO: SEND PLAYER THEIR FACTION'S SCORE
					} else {
						player.sendMessage(ChatColor.RED + "You're not in a faction!");
					}
					return true;
				}
			}
		}

		if (args.length == 4) {
			if (sender.isOp()) {
				if (args[0].equalsIgnoreCase("adm")) {
					if (args[1].equalsIgnoreCase("add")) {
						this.handleAddFactionScore(args);
						return true;
					} else if (args[1].equalsIgnoreCase("remove")) {
						this.handleRemoveFactionScore(args);
						return true;
					} else if (args[1].equalsIgnoreCase("multiplier")) {
						if (args[2].equalsIgnoreCase("add")) {
							this.handleAddMultiplier(args);
							return true;
						} else if (args[2].equalsIgnoreCase("remove")) {
							this.handleRemoveMultiplier(args);
							return true;
						}
					} else if (args[1].equalsIgnoreCase("reload")) {

					}
				}
			}
		}

		return false;
	}

	public void handleAddFactionScore(String[] args) {

	}

	public void handleRemoveFactionScore(String[] args) {

	}

	public void handleAddMultiplier(String[] args) {

	}

	public void handleRemoveMultiplier(String[] args) {

	}

}
