package net.badlion.potpvp.commands;

import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TPWarpCommand extends GCommandExecutor {

    public TPWarpCommand() {
        super(1); // 1 arg minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, final String[] args) {
	    Location warp = ArenaManager.getWarp(args[0]);

	    if (warp == null) {
		    this.player.sendMessage(ChatColor.RED + "Warp for " + args[0] + " not found.");
		    return;
	    }

	    this.player.teleport(warp);
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /tpwarp [name]");
    }

}
