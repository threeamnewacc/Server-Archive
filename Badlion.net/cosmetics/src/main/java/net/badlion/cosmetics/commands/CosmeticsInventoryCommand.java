package net.badlion.cosmetics.commands;

import net.badlion.cosmetics.inventories.CosmeticsInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CosmeticsInventoryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            CosmeticsInventory.openCosmeticInventory((Player) sender);
        }

        return true;
    }

}
