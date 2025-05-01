package net.badlion.skywarslobby.kits;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum KitType {

	NORMAL(ItemStackUtil.createItem(Material.EYE_OF_ENDER, ChatColor.GREEN + "Normal")),
	OP(ItemStackUtil.createItem(Material.EXP_BOTTLE, ChatColor.GREEN + "OP"));

	private ItemStack item;

	KitType(ItemStack item) {
		this.item = item;
	}

	public ItemStack getItem() {
		return item;
	}

	@Override
	public String toString() {
		String name = this.name();
		String s1 = name.charAt(0) + "";
		String s2 = name.substring(1);
		return s1.toUpperCase() + s2.toLowerCase();
	}

}