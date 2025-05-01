package net.badlion.gberry.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashSet;
import java.util.Set;

public class AutoMuteCommand  implements CommandExecutor {

    public static boolean automute = true; // on by default
    public static Set<String> bannedWords = new HashSet<>();

    public AutoMuteCommand() {
        //AutoMuteCommand.bannedWords.add("laglion");
        //AutoMuteCommand.bannedWords.add("blamesmelly");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
	    if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
		    StringBuilder sb = new StringBuilder(ChatColor.GREEN.toString());
		    sb.append("Auto-mute keywords: ");
		    for (String bannedWord : AutoMuteCommand.bannedWords) {
			    sb.append(bannedWord);
			    sb.append(", ");
		    }
		    String str = sb.toString();
		    sender.sendMessage(str.substring(0, str.length() - 2));
		    return true;
	    } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            AutoMuteCommand.bannedWords.add(args[1].toLowerCase());

            sender.sendMessage(ChatColor.GREEN + "Now filtering \"" + args[1] + "\"");
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            AutoMuteCommand.bannedWords.remove(args[1].toLowerCase());

            sender.sendMessage(ChatColor.GREEN + "No longer filtering \"" + args[1] + "\"");
            return true;
        }

        AutoMuteCommand.automute = !AutoMuteCommand.automute;
        sender.sendMessage(ChatColor.GREEN + "Automute turned to " + AutoMuteCommand.automute);
        return true;
    }

}
