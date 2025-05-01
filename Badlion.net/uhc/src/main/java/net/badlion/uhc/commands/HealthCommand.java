package net.badlion.uhc.commands;

import net.badlion.gberry.utils.MessageUtil;
import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class HealthCommand implements CommandExecutor {

    private static DecimalFormat formatter = new DecimalFormat("#.00");

	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args.length == 1) {
			Player player = BadlionUHC.getInstance().getServer().getPlayerExact(args[0]);

			boolean messagingDisguisedName = player != null && player.isDisguised() && player.getDisguisedName().equalsIgnoreCase(args[0]);

			if (player != null && (!player.isDisguised() || messagingDisguisedName)) {
				UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

				if (uhcPlayer.getState() == UHCPlayer.State.PLAYER || uhcPlayer.getState() == UHCPlayer.State.SPEC_IN_GAME) {
					sender.sendMessage(ChatColor.GREEN + player.getDisguisedName() + "'s Health: " + ChatColor.GOLD
							+ HealthCommand.getHeartsLeftString(ChatColor.GOLD, player.getHealth()));
				} else if (uhcPlayer.getDeathTime() != null || uhcPlayer.getState() == UHCPlayer.State.DEAD) {
					sender.sendMessage(ChatColor.RED + player.getDisguisedName() + ChatColor.GOLD + " is dead");
				} else {
					sender.sendMessage(ChatColor.RED + player.getDisguisedName() + ChatColor.GOLD + " is not playing in this UHC");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Player not found.");
			}
			return true;
		}
		return false;
	}

	public static String getHeartsLeftString(ChatColor color, double healthLeft) {
		return " (" + Math.ceil(healthLeft) / 2D + " " + MessageUtil.HEART_WITH_COLOR + color + ")";
	}

}