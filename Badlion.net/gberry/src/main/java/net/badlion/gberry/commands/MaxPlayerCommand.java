package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MaxPlayerCommand implements CommandExecutor {

	public Gberry gberry;

	public MaxPlayerCommand(Gberry gberry) {
		this.gberry = gberry;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args.length == 1) {
			int num = -1;
			try {
				num = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid usage.");
				return true;
			}

			Bukkit.getServer().setMaxPlayers(num);
		}

		return true;
	}

}
