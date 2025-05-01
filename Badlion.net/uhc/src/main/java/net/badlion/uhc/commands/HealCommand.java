package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class HealCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 1) {
            Player p = BadlionUHC.getInstance().getServer().getPlayer(args[0]);
            if (p != null) {
                p.setHealth(p.getMaxHealth());
                p.sendMessage(ChatColor.GREEN + "You have been healed!");
                sender.sendMessage(ChatColor.GREEN + "Player healed.");
                UHCPlayerManager.updateHealthScores(p);
            } else {
                sender.sendMessage(ChatColor.RED + "Player is not online.");
            }

            return true;
        }

        for (Player p : Bukkit.getOnlinePlayers()) { // Don't have to do Bukkit.getPlayerExact like we would with players map
            p.setHealth(p.getMaxHealth());
            p.sendMessage(ChatColor.GREEN + "You have been healed!");

            UHCPlayerManager.updateHealthScores(p);
        }

        return true;
    }
}
