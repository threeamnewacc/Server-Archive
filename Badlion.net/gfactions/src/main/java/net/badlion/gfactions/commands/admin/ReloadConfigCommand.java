package net.badlion.gfactions.commands.admin;

import net.badlion.gfactions.Config;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadConfigCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args.length > 0) {
			try {
				Config.reloadConfig(args[0]);
				sender.sendMessage("Configuration file reloaded successfully");
			} catch (Config.ConfigNotFoundException e) {
				sender.sendMessage("Configuration file not found, unable to load");
			}

			return true;
		}
		return false;
	}

}
