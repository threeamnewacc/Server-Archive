package net.badlion.arenalobby.commands;

import net.badlion.arenalobby.managers.MatchMakingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ViewOpponentInventoryCommand extends GCommandExecutor {

	public ViewOpponentInventoryCommand() {
		super(2); // 0 arg minimum
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		int matchId = Integer.valueOf(args[0]);
		UUID playerId = UUID.fromString(args[1]);
		MatchMakingManager.openOpponentInventory(matchId, playerId, "", player);
	}

	@Override
	public void usage(CommandSender sender) {
		// They ran this command manually
		sender.sendMessage("Unknown command. Type \"/help\" for help.");
	}

}
