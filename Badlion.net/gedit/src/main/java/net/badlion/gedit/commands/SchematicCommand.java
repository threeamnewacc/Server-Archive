package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.util.ExtensionFilter;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;

public class SchematicCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("/gschematic <list, save, load>");
            return false;
        }
        if (args[0].equalsIgnoreCase("list")) {
            File[] files = GEdit.schematics.listFiles(new ExtensionFilter(".schematic"));
            sender.sendMessage(GEdit.PREFIX + ChatColor.GOLD + files.length + " Schematics:");
            sender.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() +"----------");
            boolean white = true;
            for (File file : files) {
                sender.sendMessage((white ? ChatColor.WHITE : ChatColor.GRAY) + file.getName());
                white ^= true;
            }

        }
        return false;
    }

}
