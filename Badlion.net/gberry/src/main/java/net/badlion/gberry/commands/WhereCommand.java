package net.badlion.gberry.commands;

import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.tasks.BanEveryoneTask;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;

public class WhereCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/find [username]");
            return true;
        }

        Gberry.plugin.getServer().getScheduler().runTaskAsynchronously(Gberry.plugin, new Runnable() {
            @Override
            public void run() {
                UUID uuid = Gberry.getOfflineUUID(args[0]);
                if (uuid == null) {
                    sender.sendMessage(ChatColor.RED + "User could not be found.");
                    return;
                }

	            if (sender instanceof Player) {
		            if (!BanEveryoneTask.uuids.contains(((Player) sender).getUniqueId()) && BanEveryoneTask.uuids.contains(uuid)) {
			            sender.sendMessage(ChatColor.RED + "Fuck off, stop trying to stalk important people.");
			            return;
		            } else if (!BanEveryoneTask.uuids.contains(((Player) sender).getUniqueId())
				            && (args[0].equalsIgnoreCase("captainkickass63") || args[0].equalsIgnoreCase("gorille"))) {
			            sender.sendMessage(ChatColor.RED + "Fuck off, stop trying to stalk important people.");
			            return;
		            }
	            }

                try {
                    JSONObject result = HTTPCommon.executeGETRequest("http://" + GetCommon.getIpForDB() + ":9011/FindPlayer/" + uuid.toString() + "/8qPqqR324esK9hGrNkTzT3DUPp9UC9pC");
                    if (result != null && result.containsKey("location")) {
                        sender.sendMessage(ChatColor.GOLD + args[0] + ChatColor.DARK_GREEN + " can be found on " + ChatColor.DARK_AQUA + result.get("location"));
                        return;
                    }
                } catch (HTTPRequestFailException e) {
                    sender.sendMessage(ChatColor.RED + "Something went wrong, if this keeps happening contact a developer");
                    return;
                }

                sender.sendMessage(ChatColor.RED + "Player not found on the network.");
            }
        });

        return true;
    }

}
