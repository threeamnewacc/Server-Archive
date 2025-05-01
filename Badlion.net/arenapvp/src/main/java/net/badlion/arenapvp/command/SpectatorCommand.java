package net.badlion.arenapvp.command;

import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.TeamStateMachine;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpectatorCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Player only command");
			return false;
		}
		Player player = (Player) sender;
		if (TeamStateMachine.spectatorState.contains(player)) {
			if (args.length == 1) {
				Player target = ArenaPvP.getInstance().getServer().getPlayerExact(args[0]);
				if (target != null) {
					if (target.hasPermission("badlion.kittrial") && !player.hasPermission("badlion.kittrial") && TeamStateMachine.spectatorState.contains(target)) {
						player.sendFormattedMessage("{0}Cannot spectate this staff member at the moment.", ChatColor.RED);
					} else {
						player.teleport(target.getLocation());
					}
				} else {
					player.sendFormattedMessage("{0}Player not found.", ChatColor.RED);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Command usage: /sp <player> to spectate a player.");
			}
		} else {
			player.sendFormattedMessage("{0}You must be in spectator mode to use this command.", ChatColor.RED);
		}
		return false;
	}
}
