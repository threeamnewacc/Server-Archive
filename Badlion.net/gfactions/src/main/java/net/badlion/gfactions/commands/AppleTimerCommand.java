package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.Seconds;

public class AppleTimerCommand implements CommandExecutor {

    private GFactions plugin;

    public AppleTimerCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            Long then = this.plugin.getGodAppleBlacklist().get(((Player) sender).getUniqueId().toString());
            if (then != null) {
                DateTime now = DateTime.now();
                DateTime thendt = new DateTime(then);
                int minutes = 30 - Minutes.minutesBetween(thendt, now).getMinutes();
                if (minutes == 1) { // Grammar nazi
                    sender.sendMessage(ChatColor.YELLOW + "You cannot consume another god apple for " + (60 - Seconds.secondsBetween(thendt, now).getSeconds()) + "  seconds.");
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You cannot consume another god apple for " + minutes + " minutes.");
                }
            } else {
                sender.sendMessage(ChatColor.YELLOW + "You can consume another god apple!");
            }
        }
        return true;
    }

}
