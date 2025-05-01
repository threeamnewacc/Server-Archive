package net.badlion.mpg.commands;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WhitelistCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
	    if (!(sender instanceof Player)) {
		    sender.sendMessage("You can only use this command in-game");
		    return true;                        // TODO: HANDLE FOR DISGUISED NAMES
	    }

        // No changing the whitelist once the game has started

        if (MPG.getInstance().getServerState().ordinal() >= MPG.ServerState.GAME.ordinal()) {
            sender.sendMessage(ChatColor.RED + "Can no longer change the whitelist for this match.");
            return true;
        }

        Player player = Bukkit.getPlayer(sender.getName());
        MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage(ChatColor.GOLD + "===Whitelist Commands===");
            player.sendMessage(ChatColor.GOLD + "You can whitelist " + mpgPlayer.getAllowedWhitelistSlots() + " players total.");
            player.sendMessage(ChatColor.GOLD + "/wl list - List the players you've whitelisted");
            player.sendMessage(ChatColor.GOLD + "/wl add <player> - Adds a player to whitelist");
            player.sendMessage(ChatColor.GOLD + "/wl rm <player> - Removes a player from whitelist");
            player.sendMessage(ChatColor.GOLD + "Warning: You can only whitelist/unwhitelist players before the game starts!");
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 2) {
                int allowedSlots = mpgPlayer.getAllowedWhitelistSlots();
                if (allowedSlots == 0) {
                    player.sendMessage(ChatColor.RED + "You do not have access to this feature");
                } else {
                    if (mpgPlayer.canWhiteList()) {
                        if (mpgPlayer.whitelistPlayer(args[1].toLowerCase())) {
                            player.sendMessage(ChatColor.GREEN + "You have whitelisted " + args[1] + "!");
                        } else {
                            player.sendMessage(ChatColor.RED + "This player is already whitelisted.");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You have no slots remaining, try again next game!");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Proper command usage: \"/wl add <username>\"");
            }
        } else if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm")) {
            if (args.length == 2) {
                int allowedSlots = mpgPlayer.getAllowedWhitelistSlots();
                if (allowedSlots == 0) {
                    player.sendMessage(ChatColor.RED + "You do not have access to this feature");
                } else {
                    if (mpgPlayer.removeNameFromWhitelist(args[1])) {
                        player.sendMessage(ChatColor.GREEN + "You have un-whitelisted " + args[1] + "!");
                    } else {
                        player.sendMessage(ChatColor.RED + "This player is not whitelisted by you.");
                    }
                }
            } else {
                player.sendMessage(ChatColor.RED + "Proper command usage: \"/wl remove <username>\"");
            }
        } else if (args[0].equalsIgnoreCase("list")) {
            if (mpgPlayer.getAllowedWhitelistSlots() != 0) {
                mpgPlayer.listWhitelistedNames(player);
            } else {
                player.sendMessage(ChatColor.RED + "You cannot whitelist players. Buy a rank to earn this feature.");
            }
        }

        return true;
    }

}
