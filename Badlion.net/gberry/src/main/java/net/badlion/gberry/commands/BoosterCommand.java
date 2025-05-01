package net.badlion.gberry.commands;

import net.badlion.gberry.inventories.BoosterInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BoosterCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		Player player = (Player) sender;
		if (sender instanceof Player) {
			if (command.getName().equalsIgnoreCase("boosters")) {
				BoosterInventory.openBoosterInventory(player);
			}
		}
		return false;
	}

}