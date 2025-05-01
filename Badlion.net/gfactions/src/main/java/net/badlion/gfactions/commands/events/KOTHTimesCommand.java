package net.badlion.gfactions.commands.events;

import java.sql.Timestamp;
import java.util.Date;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KOTHTimesCommand implements CommandExecutor {

    private GFactions plugin;

    public KOTHTimesCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("deprecation")
	@Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.DARK_AQUA + "--------------------------------------------------");
            Timestamp currentTime = new java.sql.Timestamp(new Date().getTime());
			sender.sendMessage(ChatColor.GOLD + "Each KOTH happens at a random location at the set times below:");
			sender.sendMessage(ChatColor.RED + "Current time - " + currentTime.getHours() + ":" + currentTime.getMinutes());
            for (String dateTime : this.plugin.getConfig().getStringList("gfactions.koth.koth_times")) {
                sender.sendMessage(ChatColor.AQUA + " - " + dateTime + " Eastern");
            }
        } else {
            sender.sendMessage("You can only use this command in-game!");
        }
        return true;
    }

}
