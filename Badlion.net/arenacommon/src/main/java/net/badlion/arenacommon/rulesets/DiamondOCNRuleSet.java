package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class DiamondOCNRuleSet extends KitRuleSet {

    public DiamondOCNRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.DIAMOND_HELMET), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.NON_SPEED, false, false);

	    // Enable in duels
	    this.enabledInDuels = true;

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
	    this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
	    this.defaultArmorKit[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
        this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
        this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

	    // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[4] = new ItemStack(Material.COOKED_BEEF, 64);
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 32);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Diamond";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Feather";
	    this.info4Sign[2] = "Falling IV";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "No Enchants";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "No Enchants";
	    this.info6Sign[2] = "32 Arrows";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Steak";
	    this.info8Sign[2] = "";
	    this.info8Sign[3] = "";
    }

}
