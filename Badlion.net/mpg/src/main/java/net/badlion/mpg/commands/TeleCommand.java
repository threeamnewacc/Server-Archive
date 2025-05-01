package net.badlion.mpg.commands;

import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) return true;

		Player player = (Player) sender;
		MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player);

		if (mpgPlayer.getState().ordinal() < MPGPlayer.PlayerState.SPECTATOR.ordinal() || player.getGameMode() != GameMode.CREATIVE) {
			sender.sendMessage(ChatColor.RED + "You must be a spectator to use this command.");
			return true;
		}

		if (args.length == 1) {
			Player target = Bukkit.getPlayerExact(args[0]);

			if (player == target) {
				player.sendMessage(ChatColor.RED + "You cannot teleport to yourself.");
				return true;
			}

			boolean disguisedName = target != null && target.isDisguised() && target.getDisguisedName().equalsIgnoreCase(args[0]);

			// Let hosts /tele to real names even when player is disguised
			if (target == null || (!sender.hasPermission("badlion.uhchost") && target.isDisguised() && !disguisedName)) {
				sender.sendMessage(ChatColor.RED + "Player not found.");
				return true;
			}

			MPGPlayer targetMPGPlayer = MPGPlayerManager.getMPGPlayer(target);
			if (targetMPGPlayer.getState() == MPGPlayer.PlayerState.PLAYER) {
				player.teleport(target);
			} else {
				sender.sendMessage(ChatColor.RED + "You can only teleport to players who are alive.");
			}
		} else {
			return false;
		}

		return true;
	}

}
