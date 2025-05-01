package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearInventoryCommand implements CommandExecutor {
    // TODO: Gberry overriding this? dafuq
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (((Player) sender).getUniqueId().equals(BadlionUHC.getInstance().getHost().getUUID())) {
            for (Player p : Bukkit.getOnlinePlayers()) { // Don't have to do Bukkit.getPlayerExact like we would with players map
                p.getInventory().clear();
	            p.getInventory().setArmorContents(null);
	            p.sendMessage(ChatColor.GREEN + "Your inventory has been cleared!");
            }
        } else {
	        sender.sendMessage("You do not have permission to use this command");
        }
        return true;
    }
}
