package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.sessions.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Set implements CommandExecutor {

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

        if (args.length != 1) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "/gset <block:id> or <block:id>,<block:id> etc.");
            return true;
        }

        String blocks = args[0];
        SessionManager.getSession(player).set(blocks);
        return true;
    }

}
