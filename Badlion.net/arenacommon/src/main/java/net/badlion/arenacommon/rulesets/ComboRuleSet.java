package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ComboRuleSet extends KitRuleSet {

    public ComboRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.RAW_FISH , 1, (short) 3), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.SPEED_II, false, true);

	    this.enabledInEvents = false;
	    this.enabledInDuels = true;

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
	    this.defaultArmorKit[3].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
	    this.defaultArmorKit[2].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultArmorKit[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
	    this.defaultArmorKit[1].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
	    this.defaultArmorKit[0].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
	    this.defaultInventoryKit[0].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.FIRE_ASPECT, 2);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 2);

	    this.defaultInventoryKit[1] = new ItemStack(Material.GOLDEN_APPLE, 64, (short) 1);
	    this.defaultInventoryKit[2] = ItemStackUtil.STRENGTH_POTION_EXT;
	    this.defaultInventoryKit[3] = ItemStackUtil.SWIFTNESS_POTION_II_EXT;
	    this.defaultInventoryKit[4] = ItemStackUtil.STRENGTH_POTION_EXT;
	    this.defaultInventoryKit[5] = ItemStackUtil.SWIFTNESS_POTION_II_EXT;

	    this.defaultInventoryKit[9] = new ItemStack(Material.DIAMOND_HELMET);
	    this.defaultInventoryKit[9].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultInventoryKit[9].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultInventoryKit[10] = new ItemStack(Material.DIAMOND_CHESTPLATE);
	    this.defaultInventoryKit[10].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultInventoryKit[10].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultInventoryKit[11] = new ItemStack(Material.DIAMOND_LEGGINGS);
	    this.defaultInventoryKit[11].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultInventoryKit[11].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    this.defaultInventoryKit[12] = new ItemStack(Material.DIAMOND_BOOTS);
	    this.defaultInventoryKit[12].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
	    this.defaultInventoryKit[12].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);


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
	    this.info2Sign[1] = "§5Combo";
	    this.info2Sign[2] = "";
	    this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Protection IV";
	    this.info4Sign[2] = "Unbreaking X";
	    this.info4Sign[3] = "Bring 2 Sets";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "Sharpness II";
	    this.info5Sign[2] = "Fire Aspect II";
	    this.info5Sign[3] = "Unbreaking X";

	    this.info6Sign[0] = "§dPotions";
	    this.info6Sign[1] = "x2 Strength II";
	    this.info6Sign[2] = "x2 Speed II";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Gapples";
	    this.info8Sign[2] = "";
	    this.info8Sign[3] = "";

	    this.maxNoDamageTicks = 3;
    }

	@Override
	public void sendMessages(Player player) {
		player.sendMessage(ChatColor.DARK_AQUA + "You can be hit a lot faster than normal in this kit!");
	}

}
