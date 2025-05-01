package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import org.bukkit.enchantments.Enchantment;

public class CustomRuleSet extends KitRuleSet {

	public CustomRuleSet(int id, String name) {
		super(id, name, ArenaCommon.ArenaType.PEARL, KnockbackType.NON_SPEED, true, true);

		// Enable in duels
		this.enabledInDuels = true;

		// Initialize valid enchants
		this.validEnchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
		this.validEnchantments.put(Enchantment.PROTECTION_FALL, 4);
		this.validEnchantments.put(Enchantment.DAMAGE_ALL, 5);
		this.validEnchantments.put(Enchantment.KNOCKBACK, 2);
		this.validEnchantments.put(Enchantment.FIRE_ASPECT, 2);
		this.validEnchantments.put(Enchantment.DURABILITY, 3);
		this.validEnchantments.put(Enchantment.ARROW_DAMAGE, 5);
		this.validEnchantments.put(Enchantment.ARROW_KNOCKBACK, 2);
		this.validEnchantments.put(Enchantment.ARROW_FIRE, 1);
		this.validEnchantments.put(Enchantment.ARROW_INFINITE, 1);
	}

}
