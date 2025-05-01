package net.badlion.smellyshops.commands;

import net.badlion.smellyshops.ShopInfo;
import net.badlion.smellyshops.SmellyShops;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length < 4) return false;
                      // usage: /createshop <buy:sell> <amount> <price> <item_description>
            try {
                Boolean buy = args[0].equals("buy");

                int amount = Integer.valueOf(args[1]); // Amount
                int price = Integer.valueOf(args[2]); // Price

                StringBuilder sb = new StringBuilder();
                for (int x = 3; x < args.length; x++) {
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

	            SmellyShops.getInstance().getCreateShopAuthorization().put(sender.getName(),
		                new ShopInfo(buy ? "buy" : "sell", amount, price, itemDescription));

                String action = buy ? "buy" : "sell";
                sender.sendMessage(ChatColor.GREEN + "Click the sign you want to add this shop too. " +
                        "The price to " + action + " " + amount + " of the item will be " + price + ".");
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
