package net.badlion.uhc.practice;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PracticeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}

		Player player = (Player) sender;
		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer((player.getUniqueId()));

		if (uhcPlayer.getState().ordinal() >= UHCPlayer.State.MOD.ordinal()) {
			if (args.length == 0 || uhcPlayer.getState() == UHCPlayer.State.MOD) {
				player.sendMessage(ChatColor.RED + "You aren't allowed to go to the practice arena!");
			} else {
				if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("true")) {
					player.sendMessage(ChatColor.GREEN + "You have turned on practice.");
				} else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("false")) {
					player.sendMessage(ChatColor.GREEN + "You have turned off practice.");
					PracticeManager.endPractice();
				} else {
					player.sendMessage(ChatColor.RED + "You can only toggle practice with 'on/off'");
					return true;
				}

				BadlionUHC.getInstance().setPractice(Boolean.valueOf(args[0]));
			}

			return true;
		}

		if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.PRE_START) {
			if (!PracticeManager.isInPractice(uhcPlayer)) {
				// Don't allow them to join if it's disabled, but let them leave
				if (BadlionUHC.getInstance().isPractice()) {
					PracticeManager.addPlayer(uhcPlayer);
					player.sendMessage(ChatColor.GREEN + "You have joined the practice arena!");
				} else {
					player.sendMessage(ChatColor.RED + "The Practice arena is currently disabled.");
				}
			} else {
				PracticeManager.removePlayer(uhcPlayer, true);
				player.sendMessage(ChatColor.GREEN + "You have left the practice arena.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "The UHC match has already started!");
		}

		return true;
	}

}
