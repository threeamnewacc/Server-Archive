package net.badlion.smellyshops.commands;

import net.badlion.smellyshops.SmellyShops;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SellAllCommand implements CommandExecutor {

	public static ArrayList<Player> sellAllPlayers = new ArrayList<Player>();

	private ItemStack acceptItem;
	private ItemStack cancelItem;

    public SellAllCommand() {
	    ItemStack acceptItem = new ItemStack(Material.WOOL);
	    ItemMeta acceptItemMeta = acceptItem.getItemMeta();
	    acceptItemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Sell Items");
	    acceptItem.setItemMeta(acceptItemMeta);
	    acceptItem.setDurability((short) 13);
	    this.acceptItem = acceptItem;

	    ItemStack cancelItem = new ItemStack(Material.WOOL);
	    ItemMeta cancelItemMeta = cancelItem.getItemMeta();
	    cancelItemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel");
	    cancelItem.setItemMeta(cancelItemMeta);
	    cancelItem.setDurability((short) 14);
	    this.cancelItem = cancelItem;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
	    	Player player = (Player) sender;
	        Inventory inv = SmellyShops.getInstance().getServer().createInventory(null, 54, ChatColor.AQUA + ChatColor.BOLD.toString()
			        + "Insert items to sell:");

	        inv.setItem(45, this.cancelItem);
	        inv.setItem(53, this.acceptItem);
	        player.openInventory(inv);

	        SellAllCommand.sellAllPlayers.add(player);
        } else {
            sender.sendMessage("You can only use this command in-game!");
        }
        return true;
    }
}
