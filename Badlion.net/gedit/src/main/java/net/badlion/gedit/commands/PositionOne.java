package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.sessions.SessionManager;
import net.badlion.gedit.wands.SelectionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PositionOne implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(GEdit.PREFIX + "You must be a player to use that command");
            return true;
        }

        Player player = (Player) commandSender;

        SessionManager.getSession(player).getWandSelection().setPoint1(player.getLocation());
        player.sendMessage(GEdit.PREFIX + ChatColor.YELLOW + "Point 1 set at " + SelectionManager.toStringLocation(player.getLocation()));

        return true;
    }

}
