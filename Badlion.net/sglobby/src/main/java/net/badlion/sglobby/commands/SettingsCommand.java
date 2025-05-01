package net.badlion.sglobby.commands;

import net.badlion.sglobby.inventories.SGSettingsInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) return true;

		SGSettingsInventory.getInstance().openSettingsInventory((Player) sender);

		return true;
	}

}
