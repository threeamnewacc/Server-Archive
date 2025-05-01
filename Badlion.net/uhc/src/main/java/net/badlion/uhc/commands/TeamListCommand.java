package net.badlion.uhc.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamListCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;

		// Get args
		String name = "";
		if (args.length > 0) name = args[0];

		player.performCommand("team list " + name);

		return true;
	}

}
