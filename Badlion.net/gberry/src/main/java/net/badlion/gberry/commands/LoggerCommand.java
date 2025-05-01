package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LoggerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender.isOp()) {
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("add")) {
                    Gberry.loggingTags.add(args[1]);
                    sender.sendMessage(ChatColor.GREEN + args[1] + " added to loggers.");
                } else if (args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("remove")) {
                    Gberry.loggingTags.remove(args[1]);
                    sender.sendMessage(ChatColor.GREEN + args[1] + " removed from loggers.");
                } else if (args[0].equalsIgnoreCase("clear")) {
                    Gberry.loggingTags.clear();
                    sender.sendMessage(ChatColor.GREEN + "Loggers cleared");
                } else {
                    sender.sendMessage("ya dun fukd up somewhere");
                }
            }
        }
        return true;
    }

}