package net.badlion.smellyshops.commands;

import net.badlion.smellyshops.ItemShopInfo;
import net.badlion.smellyshops.SmellyShops;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CreateItemShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length < 3) return false;
                      // usage: /createitemshop <amount> <price> <item_description>
            try {
                int amount = Integer.valueOf(args[0]); // Amount
                int price = Integer.valueOf(args[1]); // Price

                StringBuilder sb = new StringBuilder();
                for (int x = 2; x < args.length; x++) {
                    sb.append(args[x]);
					sb.append(" ");
                }
				sb.substring(0, sb.length() - 1);
                String itemDescription = sb.toString();

	            // Is price too high?
				if (price > 10000000) {
					sender.sendMessage(ChatColor.RED + "Price is too high!");
					return true;
				}

	            // Is item amount too high?
				if (amount > 1000) {
					sender.sendMessage(ChatColor.RED + "Item amount is too high!");
					return true;
				}

				// Is description too long?
				if (itemDescription.length() > 20) {
					sender.sendMessage(ChatColor.RED + "Description is too long.");
					return true;
				}

                SmellyShops.getInstance().getCreateItemShopAuthorization().put(sender.getName(),
		                new ItemShopInfo(amount, itemDescription, price));

	            sender.sendMessage(ChatColor.GREEN + "Insert the item costs for " + amount + " of the item and then close the inventory.");
	            sender.sendMessage(ChatColor.GREEN + "Insert the item costs for " + amount + " of the item and then close the inventory.");
	            sender.sendMessage(ChatColor.GREEN + "Insert the item costs for " + amount + " of the item and then close the inventory.");

	            SmellyShops.getInstance().getServer().getScheduler().runTaskLater(SmellyShops.getInstance(), new Runnable() {
		            @Override
		            public void run() {
			            Inventory inventory = SmellyShops.getInstance().getServer().createInventory(null, 54, ChatColor.AQUA + "Insert Item Costs & Close");

			            ((Player) sender).openInventory(inventory);
		            }
	            }, 60L);
            } catch (NumberFormatException e) {
                // Wrong syntax
                return false;
            }
        } else {
            sender.sendMessage("You can only use this command in-game!");
        }
        return true;
    }
}
