package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.history.HistoryManager;
import net.badlion.gedit.sessions.SessionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Paste implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(GEdit.PREFIX + "You must be a player to use that command");
            return true;
        }

        Player player = (Player) commandSender;

        if (SessionManager.getSession(player).getBlockStates().isEmpty()) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "Your clip board is empty.");
            return true;
        }

        SessionManager.getSession(player).paste(player.getLocation());
        HistoryManager.savePaste(player);
        player.sendMessage(GEdit.PREFIX + ChatColor.GREEN + "Your selection has been pasted.");

        return true;
    }

}
