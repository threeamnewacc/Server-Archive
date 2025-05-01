package net.badlion.gfactions.commands.events;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.scavenge.Scavenge;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ScavengeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length > 0) {
	        if (args[0].equalsIgnoreCase("start")) {
		        if (GFactions.plugin.getScavenge() == null) {
			        sender.sendMessage(ChatColor.YELLOW + "Starting a new scavenge event");
			        new Scavenge();
		        } else {
			        sender.sendMessage(ChatColor.YELLOW + "A scavenge event is already running");
		        }
	        } else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("stop")) {
		        if (GFactions.plugin.getScavenge() != null) {
			        sender.sendMessage(ChatColor.YELLOW + "Stopping the scavenge event");
			        GFactions.plugin.getScavenge().stop(false);
		        } else {
			        sender.sendMessage(ChatColor.YELLOW + "A scavenge event is not running");
		        }
	        }
        }

        return true;
    }
}
