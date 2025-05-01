package net.badlion.gberry.commands;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ArmorSeeCommand implements CommandExecutor {

    private Gberry plugin;

    public ArmorSeeCommand(Gberry plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            if (strings.length != 1) {
                return false;
            }

            Player p = this.plugin.getServer().getPlayerExact(strings[0]);
            if (p != null) {
                Inventory inv = this.plugin.getServer().createInventory(null, 36, ChatColor.LIGHT_PURPLE + p.getName() + "'s Armor");
	            inv.addItem(p.getInventory().getArmorContents());
	            ((Player) sender).openInventory(inv);
            } else {
                sender.sendMessage(ChatColor.RED + "That player is not online!");
            }
        } else {
            sender.sendMessage("This command can only be used in-game!");
        }
        return true;
    }

}
