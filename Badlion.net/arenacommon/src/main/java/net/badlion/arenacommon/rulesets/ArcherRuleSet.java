package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArcherRuleSet extends KitRuleSet {

    public ArcherRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.BOW), ArenaCommon.ArenaType.ARCHER, KnockbackType.SPEED_II, false, false);

	    // Enable in duels
	    this.enabledInDuels = true;

	    // Speed 1
	    this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.LEATHER_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.LEATHER_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.LEATHER_BOOTS);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.ARROW_INFINITE, 1);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
	    ItemStackUtil.addUnbreaking(this.defaultInventoryKit[0]);

	    this.defaultInventoryKit[1] = new ItemStack(Material.ARROW);
        this.defaultInventoryKit[3] = new ItemStack(Material.ENDER_PEARL, 3);
        this.defaultInventoryKit[8] = new ItemStack(Material.COOKED_BEEF, 64);

        // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Archer";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dLeather Armor";
	    this.info4Sign[1] = "No Enchants";
	    this.info4Sign[2] = "";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§dBow";
	    this.info5Sign[1] = "Infinity";
        this.info5Sign[2] = "Punch II";
        this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dPotions";
	    this.info6Sign[1] = "Speed I";
        this.info6Sign[2] = "";
        this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dOther";
        this.info8Sign[1] = "3 Enderpearls";
	    this.info8Sign[2] = "64 Steak";
	    this.info8Sign[3] = "";
    }

}