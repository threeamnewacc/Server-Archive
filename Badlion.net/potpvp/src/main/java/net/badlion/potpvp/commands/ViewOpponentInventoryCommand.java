package net.badlion.potpvp.commands;

import net.badlion.potpvp.managers.MatchMakingManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ViewOpponentInventoryCommand extends GCommandExecutor {

	public ViewOpponentInventoryCommand() {
		super(0); // 0 arg minimum
	}

	@Override
	public void onGroupCommand(Command command, String label, String[] args) {
		MatchMakingManager.openOpponentInventory(this.player);
	}

	@Override
	public void usage(CommandSender sender) {
		// They ran this command manually
	 	sender.sendMessage("Unknown command. Type \"/help\" for help.");
	}

}
