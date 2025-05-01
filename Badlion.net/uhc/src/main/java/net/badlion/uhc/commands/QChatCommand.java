package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import net.badlion.uhc.listeners.gamemodes.QuadrantsGameMode;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class QChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			if (!GameModeHandler.GAME_MODES.contains("QUADRANTS")) {
				sender.sendMessage(ChatColor.RED + "Quadrants is not active.");
				return true;
			}

			if (args.length == 0) {
				sender.sendMessage("/qc [message]");
				return true;
			}

			if (!QuadrantsGameMode.playerTeams.containsKey(((Player) sender).getUniqueId())) {
				sender.sendMessage(ChatColor.RED + "You are not in a quadrant!");
				return true;
			}

			StringBuilder sb = new StringBuilder();
			for (String s2 : args) {
				sb.append(s2);
				sb.append(" ");
			}

			String message = sb.toString();
			for (UUID uuid : QuadrantsGameMode.getPlayersByTeam(QuadrantsGameMode.playerTeams.get(((Player) sender).getUniqueId()))) {
				BadlionUHC.getInstance().getServer().getPlayer(uuid).sendMessage(ChatColor.RED + "[Quadrant]" + sender.getName() + ": " + message);
			}
		}
		return true;
	}

}
