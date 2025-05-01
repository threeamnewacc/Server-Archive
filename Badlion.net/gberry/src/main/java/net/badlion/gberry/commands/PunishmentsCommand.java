package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PunishmentsCommand implements CommandExecutor {

	private Gberry plugin;

	public PunishmentsCommand(Gberry plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
		if (sender instanceof Player) {
			sender.sendMessage(ChatColor.GOLD + "Your punishments: http://www.badlion.net/profile/user/" + sender.getName() + "/punishments");
		} else {
			sender.sendMessage("This command can only be used in-game!");
		}
		return true;
	}

}
