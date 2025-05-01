package net.badlion.uhc.practice.kits;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Kit {

	public static List<Kit> kits = new ArrayList<>();

	private String name;
	private int id;

	public Kit(String name, int id) {
		this.name = name;
		this.id = id;
	}

	public static Kit getKit(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equals(name)) {
				return kit;
			}
		}
		return null;
	}

	public abstract void giveItems(Player player);

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public ItemStack enchantItem(ItemStack itemStack, Enchantment enchantment, int level) {
		itemStack.addEnchantment(enchantment, level);
		ItemStackUtil.addUnbreaking(itemStack);
		return itemStack;
	}
}
