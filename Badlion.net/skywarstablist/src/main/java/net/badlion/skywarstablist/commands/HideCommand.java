package net.badlion.skywarstablist.commands;

import net.badlion.tablist.TabListManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if(sender instanceof Player) {
			Player player = ((Player) sender);
			TabListManager.getInstance().getListCommandHandler().checkIfStaffLeft(player);
		}
		return true;
	}

}
