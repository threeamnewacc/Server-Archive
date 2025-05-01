package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.DuelHelper;
import net.badlion.arenalobby.managers.DuelRequestManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AcceptDuelCommand extends GCommandExecutor {

	public AcceptDuelCommand() {
		super(1);
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);
		DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());

		// Can only be null if 15s pass for custom kit selection and player is still in custom kit selection inventory
		// Shouldn't even reach here though
		if (duelCreator == null) {
			// Don't think they ever get a chance to reach this, but we'll find out
			Bukkit.getLogger().info("SMELLYISTHEBEST DUEL REQUEST DEBUG");
			player.sendFormattedMessage("{0}Could not find the request.", ChatColor.RED);
			player.closeInventory();
			return;
		}

		DuelHelper.handleDuelAccept(player, duelCreator);
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Command usage: /accept <player> to accept a duel request");
	}
}
