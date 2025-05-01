package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import net.badlion.gedit.sessions.SessionManager;
import net.badlion.gedit.util.SchematicUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand implements CommandExecutor {

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

        try {
            SchematicUtil.saveSession(player, args[0]);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED + "Unable to save schematic to file.");
            e.printStackTrace();
        }
        //SessionManager.getSession(player).save(args[0]);

        return true;
    }

}
