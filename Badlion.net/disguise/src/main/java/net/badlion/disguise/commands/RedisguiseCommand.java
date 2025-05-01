package net.badlion.disguise.commands;

import net.badlion.disguise.Disguise;
import net.badlion.disguise.managers.DisguiseManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RedisguiseCommand implements CommandExecutor {

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

		if (player.isDisguised()) {
			if (DisguiseManager.undisguisePlayer(player, true)) {
				DisguiseManager.disguisePlayer(player, true, true);
			}
		} else {
			DisguiseManager.disguisePlayer(player, true, false);
		}

		return true;
	}

}