package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListHideCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof Player)) return true;

	    Gberry.plugin.getListCommandHandler().removePlayerFromList(((Player) sender));

	    sender.sendMessage(ChatColor.YELLOW + "You have been hidden from /list.");

        return true;
    }

}
