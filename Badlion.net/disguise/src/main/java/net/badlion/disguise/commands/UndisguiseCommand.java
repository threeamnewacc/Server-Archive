package net.badlion.disguise.commands;

import net.badlion.disguise.Disguise;
import net.badlion.disguise.managers.DisguiseManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UndisguiseCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
	    if (!(sender instanceof Player)) return true;

	    Player player = (Player) sender;

	    // Check if commands can be used on this server
	    if (!Disguise.getInstance().areCommandsEnabled()) {
		    player.sendMessage(ChatColor.RED + "Cannot use disguise commands on this server, must be in a lobby!");
		    return true;
	    }

	    // Check command cooldown
	    if (DisguiseManager.hasCooldown(player.getUniqueId())) {
		    player.sendMessage(ChatColor.RED + "Cannot spam disguise commands, wait 5 seconds.");
		    return true;
	    }

	    // Check to see if they're not disguised
	    if (!player.isDisguised()) {
		    player.sendMessage(ChatColor.RED + "You are not disguised!");
		    return true;
	    }

	    if (DisguiseManager.undisguisePlayer(player, true)) {
		    player.sendMessage(ChatColor.GREEN + "You have disabled disguise.");
	    }

        return true;
    }

}
