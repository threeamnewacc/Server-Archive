package net.badlion.survivalgames.commands;

import net.badlion.survivalgames.SurvivalGames;
import net.badlion.survivalgames.managers.SGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class WhitelistCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        Player player = Bukkit.getPlayer(sender.getName());
        SGPlayer sgPlayer = SGPlayerManager.getSGPlayer(player.getUniqueId());
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 2) {
                if (sgPlayer.getMaxWhitelistSlots() == 0) {
                    player.sendMessage(ChatColor.RED + "You do not have access to this feature");
                } else {
                    if (sgPlayer.getWhitelistedNames().size() < sgPlayer.getMaxWhitelistSlots()) {
                            player.sendMessage(ChatColor.GREEN + "You have whitelisted " + args[1] + "!");
                            sgPlayer.getWhitelistedNames().add(args[1].toLowerCase());
                            SurvivalGames.addWhiteListedPlayer(args[1].toLowerCase());
                    } else {
                        player.sendMessage(ChatColor.RED + "You have no slots remaining, try again next game!");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Proper command usage: \"/wl add <username>\"");
            }
        } else if (args[0].equalsIgnoreCase("list")) {
            if (sgPlayer.getWhitelistedNames().size() > 0) {
                player.sendMessage(ChatColor.GREEN + "This game you have whitelisted:");
                for (String current : sgPlayer.getWhitelistedNames()) {
                    player.sendMessage(ChatColor.GREEN + current);
                }
            }
        }
        return false;
    }
}
