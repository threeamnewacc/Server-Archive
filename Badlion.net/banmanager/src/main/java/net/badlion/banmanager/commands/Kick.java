package net.badlion.banmanager.commands;

import com.google.common.base.Joiner;
import net.badlion.banmanager.BanManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class Kick implements CommandExecutor {

	private BanManager plugin;

	public Kick(BanManager plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (sender.hasPermission("bm.kick")) {
				if (args.length >= 2) {
					if (Bukkit.getPlayerExact(args[0]) != null) {
						String reason = Joiner.on(" ").skipNulls().join(Arrays.copyOfRange(args, 1, args.length));

						Kick.this.plugin.insertPunishment(sender, player.getUniqueId().toString(), BanManager.PUNISHMENT_TYPE.KICK, args[0], reason, 0);
					} else {
						sender.sendMessage(ChatColor.RED + "User is not online");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Correct usage is /kick <username> <reason>");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have access to this command!");
			}
		} else {
			if (args.length >= 2) {
				if (Bukkit.getPlayerExact(args[0]) != null) {
					String reason = Joiner.on(" ").skipNulls().join(Arrays.copyOfRange(args, 1, args.length));

					Kick.this.plugin.insertPunishment(sender, BanManager.CONSOLE_SENDER, BanManager.PUNISHMENT_TYPE.KICK, args[0], reason, 0);
				} else {
					sender.sendMessage(ChatColor.RED + "User is not online");
				}
			} else {
				sender.sendMessage("Correct usage is 'kick <username> <reason>'");
			}
		}

		return true;
	}

}
