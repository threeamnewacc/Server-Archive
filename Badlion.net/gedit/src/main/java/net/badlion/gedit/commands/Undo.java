package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.history.HistoryManager;
import net.badlion.gedit.history.PasteHistory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Undo implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(GEdit.PREFIX + "You must be a player to use that command");
            return true;
        }

        Player player = (Player) commandSender;

        List<PasteHistory> history = HistoryManager.getHistory(player);

        if (history == null || history.isEmpty()) {
            player.sendMessage(GEdit.PREFIX + ChatColor.RED + "Nothing left to undo.");
            return true;
        }

        history.get(history.size() - 1).getSession().undo();
        history.remove(history.size() - 1);

        player.sendMessage(GEdit.PREFIX + ChatColor.GOLD + "Changes restored!");
        return true;
    }

}
