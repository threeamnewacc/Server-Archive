package us.zonix.core.client;

import org.bukkit.ChatColor;
import us.zonix.core.CorePlugin;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public final class CosmeticCommand extends BaseCommand {

	@Command(name = "cosmetic", rank = Rank.DEVELOPER)
	@Override public void onCommand(CommandArgs command) {
		if (command.isPlayer() && !command.getPlayer().isOp()) {
			command.getPlayer().sendMessage(ChatColor.RED + "Insufficient permissions.");
			return;
		}

		String[] args = command.getArgs();

		if (args.length == 0) {
			command.getSender().sendMessage(ChatColor.RED + "Usage: /cosmetic [args...]");
			return;
		}

		String subCommand = args[0].toLowerCase();

		switch (subCommand) {
			case "add": // /cosmetic add [player] [type]
			case "remove": // /cosmetic remove [player] [type]
				if (args.length < 3) {
					command.getSender().sendMessage(ChatColor.RED + "Usage: /cosmetic " + subCommand +
					                                " <player> <type> [args...]");
					return;
				}

				String player = args[1];
				String type = args[2];
				String data = null;

				if (type.equalsIgnoreCase("cape")) {
					if (args.length < 4) {
						command.getSender().sendMessage(ChatColor.RED + "Usage: /cosmetic " + subCommand +
						                                " <player> cape <name>");
						return;
					}

					data = args[3];
				}

				CorePlugin.getInstance().getRequestProcessor().sendRequestAsync(new CosmeticRequest(
						subCommand, player, type, data == null ? "null" : data
				));

				String added = subCommand.equals("add") ? "Added" : "Removed";
				String typed = type.equalsIgnoreCase("cape") ? data + " Cape" : type;
				command.getSender().sendMessage(ChatColor.GREEN + added + " " + typed + " to " + player);
				break;
			default:
				command.getSender().sendMessage(ChatColor.RED + "Invalid sub-command. Try add or remove.");
				break;
		}
	}

}
