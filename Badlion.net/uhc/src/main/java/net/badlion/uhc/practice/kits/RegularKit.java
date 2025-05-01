package net.badlion.uhc.practice.kits;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RegularKit extends Kit {

	public RegularKit() {
		super("uhc", 2);
	}

	@Override
	public void giveItems(Player player) {
		// Clear items
		player.getInventory().clear();

		// Set items
		player.getInventory().setItem(0, this.enchantItem(new ItemStack(Material.IRON_SWORD), Enchantment.DAMAGE_ALL, 1));
		player.getInventory().setItem(1, this.enchantItem(new ItemStack(Material.BOW), Enchantment.ARROW_DAMAGE, 1));
		player.getInventory().setItem(1, this.enchantItem(player.getInventory().getItem(1), Enchantment.ARROW_INFINITE, 1));
		player.getInventory().setItem(2, new ItemStack(Material.FISHING_ROD));
		player.getInventory().setItem(3, ItemStackUtil.createItem(Material.WATER_BUCKET, ChatColor.GREEN + "MLG Bucket"));
		player.getInventory().setItem(8, new ItemStack(Material.ARROW));

		// Set armour
		player.getInventory().setArmorContents(
				new ItemStack[] {
						this.enchantItem(new ItemStack(Material.IRON_BOOTS), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
						this.enchantItem(new ItemStack(Material.IRON_LEGGINGS), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
						this.enchantItem(new ItemStack(Material.IRON_CHESTPLATE), Enchantment.PROTECTION_ENVIRONMENTAL, 1),
						this.enchantItem(new ItemStack(Material.IRON_HELMET), Enchantment.PROTECTION_ENVIRONMENTAL, 1)
				}
		);

		// Inventory cleanup
		player.getInventory().setHeldItemSlot(0);
		player.updateInventory();
	}

}
