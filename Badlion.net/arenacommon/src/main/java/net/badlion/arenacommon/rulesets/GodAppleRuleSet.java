package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class GodAppleRuleSet extends KitRuleSet {

    public GodAppleRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.SPEED_II, false, true);

	    // Enable in duels
	    this.enabledInDuels = true;

	    // Speed 2 and strength 2
	    this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
	    this.potionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 1));

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

	    // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 5);

	    this.defaultInventoryKit[1] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);

        this.defaultInventoryKit[2] = new ItemStack(Material.DIAMOND_HELMET);
        this.defaultInventoryKit[2].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultInventoryKit[3] = new ItemStack(Material.DIAMOND_CHESTPLATE);
        this.defaultInventoryKit[3].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultInventoryKit[4] = new ItemStack(Material.DIAMOND_LEGGINGS);
        this.defaultInventoryKit[4].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[4].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.defaultInventoryKit[5] = new ItemStack(Material.DIAMOND_BOOTS);
        this.defaultInventoryKit[5].addEnchantment(Enchantment.DURABILITY, 3);
        this.defaultInventoryKit[5].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_HELMET);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_CHESTPLATE);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_LEGGINGS);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_BOOTS);
        this.kitCreationInventory.addItem(ItemStackUtil.DIAMOND_SWORD);
        this.kitCreationInventory.addItem(ItemStackUtil.GOD_APPLE);
        this.kitCreationInventory.addItem(ItemStackUtil.STRENGTH_POTION_II_EXT);
        this.kitCreationInventory.addItem(ItemStackUtil.SWIFTNESS_POTION_II_EXT);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Gapple";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Protection IV";
	    this.info4Sign[2] = "Unbreaking III";
	    this.info4Sign[3] = "Bring 2 Sets";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "Sharpness V";
	    this.info5Sign[2] = "Fire Aspect II";
	    this.info5Sign[3] = "Unbreaking III";

	    this.info6Sign[0] = "§dPotions";
	    this.info6Sign[1] = "x2 Strength II";
	    this.info6Sign[2] = "x2 Speed II";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Gapples";
	    this.info8Sign[2] = "";
	    this.info8Sign[3] = "";
    }

}
