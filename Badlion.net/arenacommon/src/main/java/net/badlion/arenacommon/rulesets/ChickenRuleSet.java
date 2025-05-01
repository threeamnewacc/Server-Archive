package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ChickenRuleSet extends KitRuleSet {

    public ChickenRuleSet(int id, String name) {
	    super(id, name, new ItemStack(Material.RAW_CHICKEN), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.SPEED_II, false, false);

	    // Speed 2
	    this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.RAW_CHICKEN);
	    this.defaultInventoryKit[0].addUnsafeEnchantment(Enchantment.KNOCKBACK, 10);
    }

}
