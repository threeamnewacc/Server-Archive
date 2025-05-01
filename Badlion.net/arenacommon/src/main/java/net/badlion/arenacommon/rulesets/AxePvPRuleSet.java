package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AxePvPRuleSet extends KitRuleSet {

	public AxePvPRuleSet(int id, String name) {
		super(id, name, new ItemStack(Material.IRON_AXE), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.SPEED_II, false, false);

		// Enable in duels
		this.enabledInDuels = true;

		// Speed 2
		this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

		// Create default armor kit
		this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
		this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
		this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
		this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);

		// Create default inventory kit
		this.defaultInventoryKit[0] = new ItemStack(Material.IRON_AXE);
		this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);
		ItemStackUtil.addUnbreaking(this.defaultInventoryKit[0]);

		this.defaultInventoryKit[1] = new ItemStack(Material.GOLDEN_APPLE, 16);
		this.defaultInventoryKit[2] = ItemStackUtil.HEALING_SPLASH_II;
		this.defaultInventoryKit[3] = ItemStackUtil.HEALING_SPLASH_II;
		this.defaultInventoryKit[4] = ItemStackUtil.HEALING_SPLASH_II;
		this.defaultInventoryKit[5] = ItemStackUtil.HEALING_SPLASH_II;
		this.defaultInventoryKit[6] = ItemStackUtil.HEALING_SPLASH_II;
		this.defaultInventoryKit[7] = ItemStackUtil.HEALING_SPLASH_II;
		this.defaultInventoryKit[8] = ItemStackUtil.HEALING_SPLASH_II;

		// Initialize info signs
		this.info2Sign[0] = "================";
		this.info2Sign[1] = "§5Axe PvP";
		this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

		this.info4Sign[0] = "§dIron Armor";
		this.info4Sign[1] = "No Enchants";
		this.info4Sign[2] = "";
		this.info4Sign[3] = "";

		this.info5Sign[0] = "§dAxe";
		this.info5Sign[1] = "Sharp I";
		this.info5Sign[2] = "";
		this.info5Sign[3] = "";

		this.info6Sign[0] = "§dPotions";
		this.info6Sign[1] = "2 Speed II";
		this.info6Sign[2] = "7 Health II";
		this.info6Sign[3] = "";

		this.info8Sign[0] = "§dOther";
		this.info8Sign[1] = "16 Gold Apples";
		this.info8Sign[2] = "";
		this.info8Sign[3] = "";
	}
}
