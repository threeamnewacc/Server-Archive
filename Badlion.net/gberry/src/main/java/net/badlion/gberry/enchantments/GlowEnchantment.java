package net.badlion.gberry.enchantments;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;

public class GlowEnchantment extends Enchantment {

	public GlowEnchantment() {
		super(60);
	}

	@Override
	public String getName() {
		return "Custom Enchantment";
	}

	@Override
	public int getMaxLevel() {
		return 0;
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return null;
	}

	@Override
	public boolean conflictsWith(Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return false;
	}
}
