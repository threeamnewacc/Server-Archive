package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        for (Player p : Bukkit.getOnlinePlayers()) { // Don't have to do Bukkit.getPlayerExact like we would with players map
            p.setFoodLevel(20);
            p.setSaturation(20);
            p.setExhaustion(0);
            p.sendMessage(ChatColor.GREEN + "Your hunger has been restored!");
        }

        return true;
    }

}
