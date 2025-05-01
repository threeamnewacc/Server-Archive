package net.badlion.uhc.commands;

import net.badlion.disguise.managers.DisguiseManager;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.UHCTeam;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static net.badlion.gberry.Gberry.ServerType.UHC;

public class KillCountCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			if (args.length != 1) {
				return false;
			}

			if (BadlionUHC.getInstance().getState() != BadlionUHC.BadlionUHCState.STARTED) {
				sender.sendMessage(ChatColor.RED + "The UHC hasn't started yet.");
				return true;
			}

			UUID uuid = BadlionUHC.getInstance().getUUID(args[0]);
			if (uuid == null) {
				sender.sendMessage(ChatColor.RED + "Player not found.");
				return true;
			}

			UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(uuid);
			if (uhcPlayer == null) {
				sender.sendMessage(ChatColor.RED + "Player not found.");
				return true;
			}

			String username = BadlionUHC.getInstance().getUsername(uuid);

			// Check if this player has a disguised name
			if (DisguiseManager.getDisguisePlayer(uuid) != null) {
				username = DisguiseManager.getDisguisePlayer(uuid).getDisguisedName();
			}

			if (BadlionUHC.getInstance().getGameType() != UHCTeam.GameType.SOLO) {
				if (uhcPlayer.getKills() == 1) {
					sender.sendMessage(ChatColor.GREEN + username + " has " + uhcPlayer.getKills() + " kill.");
				} else {
					sender.sendMessage(ChatColor.GREEN + username + " has " + uhcPlayer.getKills() + " kills.");
				}

				if (uhcPlayer.getTeam().getKills() == 1) {
					sender.sendMessage(ChatColor.GREEN + username + "'s team has " + uhcPlayer.getTeam().getKills() + " kill.");
				} else {
					sender.sendMessage(ChatColor.GREEN + username + "'s team has " + uhcPlayer.getTeam().getKills() + " kills.");
				}
			} else {
				if (uhcPlayer.getTeam().getKills() == 1) {
					sender.sendMessage(ChatColor.GREEN + username + " has " + uhcPlayer.getKills() + " kill.");
				} else {
					sender.sendMessage(ChatColor.GREEN + username + " has " + uhcPlayer.getKills() + " kills.");
				}
			}
		}
		return true;
	}

}
