package net.badlion.smellyloot.commands;

import net.badlion.smellyloot.SmellyLoot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadDropsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		SmellyLoot.getInstance().loadLootConfig();

		sender.sendMessage("Loot tables and event drops reloaded");
		return true;
	}

}
