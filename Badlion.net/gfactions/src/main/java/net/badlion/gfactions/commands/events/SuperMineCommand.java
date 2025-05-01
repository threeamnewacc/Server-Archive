package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.supermine.SuperMine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SuperMineCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length > 0) {
	        if (args[0].equalsIgnoreCase("start")) {
		        if (GFactions.plugin.getSuperMine() == null) {
			        sender.sendMessage(ChatColor.YELLOW + "Starting a new supermine event");
			        //new SuperMine();
		        } else {
			        sender.sendMessage(ChatColor.YELLOW + "A supermine event is already running");
		        }
	        } else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("stop")) {
		        if (GFactions.plugin.getSuperMine() != null) {
			        sender.sendMessage(ChatColor.YELLOW + "Stopping the supermine event");
			        GFactions.plugin.getSuperMine().stop();
		        } else {
			        sender.sendMessage(ChatColor.YELLOW + "A supermine event is not running");
		        }
	        }
        }

        return true;
    }
}
