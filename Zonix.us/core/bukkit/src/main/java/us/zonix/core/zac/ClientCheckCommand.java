package us.zonix.core.zac;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.zonix.core.CorePlugin;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class ClientCheckCommand extends BaseCommand {

	@Command(name = "zac", rank = Rank.DEVELOPER)
	public void onCommand(CommandArgs command) {
		CommandSender sender = command.getSender();
		String[] args = command.getArgs();

		Player target;

		if (args.length == 0) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				sender.sendFormattedMessage("Usage: /zac [player]");
				return;
			}
		} else {
			target = Bukkit.getPlayer(args[0]);

			if (target == null) {
				sender.sendFormattedMessage("{0}Player not found.", ChatColor.RED);
				return;
			}
		}

		Player finalTarget = target;

		CorePlugin.getInstance().getClientProcessor().sendRequestAsync(new SessionCheckRequest(target.getUniqueId()),
				object -> {
					if (object.getAsJsonObject().get("value").getAsBoolean()) {
						sender.sendMessage(ChatColor.GOLD + finalTarget.getName() +
						                   " is currently running Zonix Anti-Cheat");
					} else {
						sender.sendMessage(ChatColor.RED + finalTarget.getName() +
						                   " is not currently running Zonix Anti-Cheat");
					}
				});
	}

}
