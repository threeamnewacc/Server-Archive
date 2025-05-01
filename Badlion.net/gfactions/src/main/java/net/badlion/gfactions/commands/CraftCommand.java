package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftCommand implements CommandExecutor {

	private GFactions plugin;

	public CraftCommand(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			player.openWorkbench(null, true);
		}

		return true;
	}

}
