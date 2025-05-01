package net.badlion.uhc.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StickCommand implements CommandExecutor {

    private ItemStack almightyStick;

    public StickCommand() {
        ItemStack stick = new ItemStack(Material.STICK);

        ItemMeta meta = stick.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Team Management Stick");

        List<String> lore = new ArrayList<String>();
        lore.add(ChatColor.RESET + ChatColor.GREEN.toString() + "Right click a player with this");
        lore.add(ChatColor.RESET + ChatColor.GREEN.toString() + "stick to invite them to your team!");
        meta.setLore(lore);

        stick.setItemMeta(meta);

        this.almightyStick = stick;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (((Player) sender).getInventory().firstEmpty() != -1) {
                ((Player) sender).getInventory().addItem(this.almightyStick);
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have enough space in your inventory");
            }
        }
        return true;
    }

}
