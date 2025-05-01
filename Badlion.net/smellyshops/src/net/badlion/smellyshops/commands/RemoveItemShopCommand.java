package net.badlion.smellyshops.commands;

import net.badlion.smellyshops.SmellyShops;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RemoveItemShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
	        SmellyShops.getInstance().getRemoveItemShopAuthorization().add(sender.getName());

            sender.sendMessage(ChatColor.GREEN + "Click the shop sign you want to remove");
        } else {
            sender.sendMessage("You can only use this command in-game!");
        }
        return true;
    }

}
