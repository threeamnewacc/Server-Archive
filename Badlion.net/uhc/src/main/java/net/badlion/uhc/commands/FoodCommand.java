package net.badlion.uhc.commands;

import net.badlion.uhc.BadlionUHC;
import net.badlion.uhc.UHCPlayer;
import net.badlion.uhc.managers.UHCPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FoodCommand implements CommandExecutor {

    public static int lastFoodAmountGiven = 0;

    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        try {
            if (args.length != 1) {
                throw new NumberFormatException();
            }

            int amount = Integer.valueOf(args[0]);

            if (amount > 64 || amount < 1) {
                sender.sendMessage(ChatColor.RED + "Number of steak must be between 1 and 64");
                return true;
            }

            ItemStack beef = new ItemStack(Material.COOKED_BEEF, amount);
            FoodCommand.lastFoodAmountGiven = amount;

            for (UHCPlayer uhcp : UHCPlayerManager.getUHCPlayersByState(UHCPlayer.State.PLAYER)) {
                Player p = BadlionUHC.getInstance().getServer().getPlayer(uhcp.getUUID());

                // Might be offline
                if (p != null) {

                    // Inventory shouldn't be full when using this command
                    if (p.getInventory().firstEmpty() != -1) {
                        p.getInventory().addItem(beef);
                        uhcp.setWasFed(true);
                        p.sendMessage(ChatColor.GREEN + "You have gotten " + amount + " beef");
                    }
                }
            }
            sender.sendMessage(ChatColor.GREEN + "Gave every player " + amount + " beef");
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

}
