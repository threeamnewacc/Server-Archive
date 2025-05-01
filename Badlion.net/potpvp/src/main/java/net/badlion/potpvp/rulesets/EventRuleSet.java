package net.badlion.potpvp.rulesets;

import org.bukkit.enchantments.Enchantment;

public class EventRuleSet extends CustomRuleSet {

	public EventRuleSet(int id, String name) {
		super(id, name);

		this.is1_9Compatible = true;

		// Initialize valid enchants
		this.validEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 10);
		this.validEnchantments.put(Enchantment.PROTECTION_FALL, 10);
		this.validEnchantments.put(Enchantment.DAMAGE_ALL, 10);
		this.validEnchantments.put(Enchantment.KNOCKBACK, 10);
		this.validEnchantments.put(Enchantment.FIRE_ASPECT, 10);
		this.validEnchantments.put(Enchantment.DURABILITY, 10);
		this.validEnchantments.put(Enchantment.ARROW_DAMAGE, 10);
		this.validEnchantments.put(Enchantment.ARROW_KNOCKBACK, 10);
		this.validEnchantments.put(Enchantment.ARROW_FIRE, 10);
		this.validEnchantments.put(Enchantment.ARROW_INFINITE, 1);
	}

}
