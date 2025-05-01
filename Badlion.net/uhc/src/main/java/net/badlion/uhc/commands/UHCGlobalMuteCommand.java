package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UHCGlobalMuteCommand implements CommandExecutor {


    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
	    // Is this player the host?
	    if (BadlionUHC.getInstance().getHost() != null && BadlionUHC.getInstance().getHost().getUUID().equals(((Player) sender).getUniqueId())) {
		    BadlionUHC.getInstance().getServer().dispatchCommand(BadlionUHC.getInstance().getServer().getConsoleSender(), "gm");
	    } else {
		    sender.sendMessage(ChatColor.RED + "You must be the host to use this command.");
	    }

        return true;
    }

}
