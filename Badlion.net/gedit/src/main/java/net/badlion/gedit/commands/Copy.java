package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.sessions.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Copy implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(GEdit.PREFIX + "You must be a player to use that command");
            return true;
        }

        Player player = (Player) commandSender;

        if (!SessionManager.getSession(player).getWandSelection().isValidSelection()) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "Your wand selection is invalid!");
            return true;
        }

        SessionManager.getSession(player).copy(player.getLocation());
        player.sendMessage(GEdit.PREFIX + ChatColor.BLUE + "Your selection of " + SessionManager.getSession(player).getWandSelection().getAllBlocks().size() + " blocks has been copied.");
        return true;
    }

}
