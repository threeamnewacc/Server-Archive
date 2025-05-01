package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeEnchantCommand implements CommandExecutor {

	private GFactions plugin;

	public DeEnchantCommand(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			// Are we combat tagged?
			if (this.plugin.isInCombat(player)) {
				player.sendMessage(ChatColor.RED + "Cannot use /deenchant when in combat.");
				return true;
			}

			// Don't let player run command if they're tagged for Man Hunt
			if (this.plugin.getManHuntTagged() != null && this.plugin.getManHuntTagged().equals(player)) {
				player.sendMessage(ChatColor.RED + "You can not use this command while tagged for Man Hunt!");
				return true;
			}

			ItemStack item = player.getItemInHand();

            if (item.getType().equals(Material.ENCHANTED_BOOK)) {
                item.setType(Material.BOOK);
            } else {
                for (Enchantment enchantment : item.getEnchantments().keySet()) {
                    item.removeEnchantment(enchantment);
                }
            }

			player.setItemInHand(item);
			player.sendMessage(ChatColor.GREEN + "Item successfully de-enchanted.");
		}

		return true;
	}

}
