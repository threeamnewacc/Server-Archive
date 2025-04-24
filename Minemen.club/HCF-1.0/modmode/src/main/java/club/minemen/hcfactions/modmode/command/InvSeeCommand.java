package club.minemen.hcfactions.modmode.command;

import club.minemen.core.rank.Rank;
import club.minemen.core.util.ItemBuilder;
import club.minemen.core.util.cmd.CommandHandler;
import club.minemen.core.util.cmd.annotation.Param;
import club.minemen.core.util.cmd.annotation.commandTypes.Command;
import club.minemen.hcfactions.modmode.profile.Profile;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InvSeeCommand implements CommandHandler {

	@Command(name = "invsee", rank = Rank.SR_MOD, description = "View another player's inventory")
	public void invseeCommand(Player sender, @Param(name = "target") String name) {
		Profile profile = Profile.getByUuid(sender.getUniqueId());

		if (profile == null) {
			sender.sendMessage(ChatColor.RED + "You cannot use this command unless you are in Mod Mode.");
		}
		else {
			Player target = Bukkit.getPlayer(name);

			if (target == null) {
				sender.sendMessage(ChatColor.RED + "Could not find that target.");
			}
			else {
				Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + ChatColor.BOLD.toString() + "Viewing Inventory of " + target.getName());

				for (ItemStack itemStack : target.getInventory().getContents()) {
					inventory.addItem(itemStack);
				}

				inventory.setItem(37, target.getInventory().getHelmet());
				inventory.setItem(38, target.getInventory().getChestplate());
				inventory.setItem(39, target.getInventory().getLeggings());
				inventory.setItem(40, target.getInventory().getBoots());

				inventory.setItem(55, new ItemBuilder(Material.PAPER).name(ChatColor.GOLD + "Location").lore(Arrays.asList(
						ChatColor.YELLOW + "World: " + ChatColor.GRAY + target.getLocation().getWorld().getName(),
						ChatColor.YELLOW + "X: " + ChatColor.GRAY + target.getX(),
						ChatColor.YELLOW + "Y: " + ChatColor.GRAY + target.getY(),
						ChatColor.YELLOW + "Z: " + ChatColor.GRAY + target.getZ()
				)).build());
			}
		}
	}

}
