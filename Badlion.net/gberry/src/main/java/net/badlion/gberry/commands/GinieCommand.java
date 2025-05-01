package net.badlion.gberry.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GinieCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
	    if (sender instanceof Player) {
		    Player player = (Player) sender;
		    player.performCommand("sudo ad off");
		    player.performCommand("sudo mc off");
		    player.performCommand("sudo report off");
		    player.sendMessage(ChatColor.YELLOW + "Turned everything off you idiot");
	    }
	    return true;
    }

}
