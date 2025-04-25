package us.zonix.core.misc.command.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import us.zonix.core.rank.Rank;
import us.zonix.core.util.command.BaseCommand;
import us.zonix.core.util.command.Command;
import us.zonix.core.util.command.CommandArgs;

public class AlertCommand extends BaseCommand {

    @Command(name = "alertsilent", rank = Rank.MANAGER)

    public void onCommand(CommandArgs command) {
        if (command.getArgs().length == 0) {
            command.getSender().sendMessage(ChatColor.RED + "Usage: /alertsilent <message>");
            return;
        }

        String message = "";

        for (int i = 0; i < command.getArgs().length; i++) {
            message += command.getArgs()[i] + " ";
        }

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
