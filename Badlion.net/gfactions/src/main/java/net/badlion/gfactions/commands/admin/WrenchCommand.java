package net.badlion.gfactions.commands.admin;

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

public class WrenchCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (sender instanceof Player) {
			ItemStack wrench = new ItemStack(Material.DIAMOND_HOE);
			ItemMeta wrenchMeta = wrench.getItemMeta();
			wrenchMeta.setDisplayName(ChatColor.GREEN + "Wrench");

			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.AQUA + "Uses:");
			lore.add(ChatColor.GOLD + "Mob Spawner: 1");
			lore.add(ChatColor.GOLD + "End Portal Frame: 6");
			lore.add(ChatColor.AQUA + "Right click on block to use");

			wrenchMeta.setLore(lore);
			wrench.setItemMeta(wrenchMeta);

			((Player) sender).getInventory().addItem(wrench);

			sender.sendMessage(ChatColor.GREEN + "You have spawned a wrench!");
		}
		return true;
	}
}
