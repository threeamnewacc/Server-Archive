package net.badlion.smellyshops.commands;

import net.badlion.smellyshops.RepairShopInfo;
import net.badlion.smellyshops.SmellyShops;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreateRepairShopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (sender instanceof Player) {
            if (args.length != 2) return false;
                      // usage: /createrepairshop <durability> <price>
            try {
	            int durability = Integer.valueOf(args[0]); // Durability
	            int price = Integer.valueOf(args[1]); // Price

	            // Is durability too high?
	            if (durability > 3000) {
		            sender.sendMessage(ChatColor.RED + "Durability is too high!");
		            return true;
	            }

	            // Is price too high?
	            if (price > 1000) {
		            sender.sendMessage(ChatColor.RED + "Price is too high!");
		            return true;
	            }

	            SmellyShops.getInstance().getCreateRepairShopAuthorization().put(sender.getName(),
			            new RepairShopInfo(durability, price));

	            sender.sendMessage(ChatColor.GREEN + "You have successfully created a repair shop. Players can repair "
			            + durability + " durability on their items for $" + price + ".");
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
