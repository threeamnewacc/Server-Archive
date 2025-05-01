package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.history.HistoryManager;
import net.badlion.gedit.sessions.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Stack implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(GEdit.PREFIX + "You must be a player to use that command");
            return true;
        }

        Player player = (Player)commandSender;

        if (!SessionManager.getSession(player).getWandSelection().isValidSelection()) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "Your wand selection is invalid!");
            return true;
        }

        if (SessionManager.getSession(player).getBlockStates().isEmpty()) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "Your clip board is empty.");
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "/gstack [# of block offset] [# of times]");
            return true;
        }
        int offset;
        int times;

        try{
            offset = Integer.parseInt(args[0]);
            times = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "Please only use numbers as input.");
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "/gstack [# of block offset] [# of times]");
            return true;
        }

        SessionManager.getSession(player).stack(offset,times);
        HistoryManager.savePaste(player);

        player.sendMessage(GEdit.PREFIX + ChatColor.LIGHT_PURPLE + "Your selection has been stacked.");
        player.sendMessage(GEdit.PREFIX + ChatColor.LIGHT_PURPLE + "Offset: " + offset + " Times: " + times);
        return true;
    }

}
