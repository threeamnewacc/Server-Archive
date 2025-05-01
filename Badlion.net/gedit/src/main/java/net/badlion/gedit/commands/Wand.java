package net.badlion.gedit.commands;

import net.badlion.gedit.GEdit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Wand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(GEdit.PREFIX + "You must be a player to use that command");
            return true;
        }

        Player player = (Player) commandSender;

        player.getInventory().addItem(new ItemStack(Material.GOLD_AXE));

        player.sendMessage(GEdit.PREFIX + ChatColor.BLUE + "You hit me in my spot.");
        return true;
    }

}
