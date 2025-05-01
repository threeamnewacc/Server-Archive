package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.commands.handlers.GameModeHandler;
import net.badlion.uhc.listeners.gamemodes.QuadrantsGameMode;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QTeamCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("You cannot use this command.");
			return true;
		}

		Player player = (Player) sender;

		UHCPlayer uhcPlayer = UHCPlayerManager.getUHCPlayer(player.getUniqueId());

		// Hosts/mods can't use this
		if (!player.isOp() && uhcPlayer.getState().ordinal() >= UHCPlayer.State.MOD.ordinal()) {
			player.sendMessage(ChatColor.RED + "You are not allowed to use this command.");
			return true;
		}

		// If it's not in-game, let them choose a team
		if (GameModeHandler.GAME_MODES.contains("QUADRANTS")) {
			if (BadlionUHC.getInstance().getState() == BadlionUHC.BadlionUHCState.PRE_START) {
				player.openInventory(QuadrantsGameMode.getTeamsInventory());
			} else {
				player.sendMessage(ChatColor.RED + "You can't choose a team at this time!");
			}
		} else {
			player.sendMessage(ChatColor.RED + "There is no game mode active that requires you to choose a team!");
		}

		return true;
	}
}
