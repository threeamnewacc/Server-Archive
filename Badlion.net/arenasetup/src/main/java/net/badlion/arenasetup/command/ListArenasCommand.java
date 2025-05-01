package net.badlion.arenasetup.command;

import net.badlion.arenasetup.manager.ArenaManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListArenasCommand  implements CommandExecutor{
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if(player.isOp()){
				ArenaManager.listAllArenas(player);
			}
		}
		return false;
	}
}
