package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class TankRuleSet extends RevertedPotionRuleSet {

    public TankRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.DIAMOND_CHESTPLATE), ArenaManager.ArenaType.PEARL, false, false);

        //Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
        //Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
	    this.defaultArmorKit[3].addEnchantment(Enchantment.DURABILITY, 3);
	    this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
	    this.defaultArmorKit[2].addEnchantment(Enchantment.DURABILITY, 3);
	    this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultArmorKit[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
	    this.defaultArmorKit[1].addEnchantment(Enchantment.DURABILITY, 3);
	    this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.DURABILITY, 3);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.DURABILITY, 3);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);

	    this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_INFINITE, 1);
	    this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_FIRE, 1);
	    this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 4);
	    this.defaultInventoryKit[1].addEnchantment(Enchantment.DURABILITY, 3);

	    this.defaultInventoryKit[2] = new ItemStack(Material.ENDER_PEARL, 16);
	    this.defaultInventoryKit[3] = new ItemStack(Material.COOKED_BEEF, 64);
	    this.defaultInventoryKit[4] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[5] = ItemStackUtil.FIRE_RESISTANCE_POTION_EXT;
	    this.defaultInventoryKit[6] = ItemStackUtil.REGENERATION_POTION_EXT;
	    this.defaultInventoryKit[7] = ItemStackUtil.STRENGTH_POTION_II;
	    this.defaultInventoryKit[8] = ItemStackUtil.SWIFTNESS_POTION_II;

	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 64);
	    this.defaultInventoryKit[10] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[11] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[12] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[13] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[14] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[15] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[16] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[17] = ItemStackUtil.HEALING_SPLASH_II;

	    this.defaultInventoryKit[18] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[19] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[20] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[21] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[22] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[23] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[24] = ItemStackUtil.REGENERATION_POTION_EXT;
	    this.defaultInventoryKit[25] = ItemStackUtil.STRENGTH_POTION_EXT;
	    this.defaultInventoryKit[26] = ItemStackUtil.SWIFTNESS_POTION_II;

	    this.defaultInventoryKit[27] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[28] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[29] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[30] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[31] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[32] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[33] = ItemStackUtil.HEALING_SPLASH_II;
	    this.defaultInventoryKit[34] = ItemStackUtil.STRENGTH_POTION_EXT;
	    this.defaultInventoryKit[35] = ItemStackUtil.SWIFTNESS_POTION_EXT;

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Tank";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Protection IV";
	    this.info4Sign[2] = "Unbreaking III";
	    this.info4Sign[3] = "FF IV";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "Sharpness III";
	    this.info5Sign[2] = "Fire Aspect II";
	    this.info5Sign[3] = "Unbreaking III";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "Power 4";
	    this.info6Sign[2] = "Flame/Infinity 1";
	    this.info6Sign[3] = "Unbreaking III";

	    this.info8Sign[0] = "§dAllowed Items";
	    this.info8Sign[1] = "Food / Pearls";
	    this.info8Sign[2] = "Most Potions";
	    this.info8Sign[3] = "No GApples";

		// Knockback changes
		this.knockbackFriction = 2.0;
		this.knockbackHorizontal = 0.34;
		this.knockbackVertical = 0.34;
		this.knockbackVerticalLimit = 0.4;
		this.knockbackExtraHorizontal = 0.425;
		this.knockbackExtraVertical = 0.085;
    }

}
