package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MineZRuleSet extends KitRuleSet {

    public static HashSet<Material> types = new HashSet<>();
    public static Map<Material, Integer> mappedStacks = new HashMap<>();

    static {
        MineZRuleSet.types.add(Material.ARROW);
        MineZRuleSet.types.add(Material.BREAD);
        MineZRuleSet.types.add(Material.GOLDEN_APPLE);
        MineZRuleSet.mappedStacks.put(Material.ARROW, 15);
        MineZRuleSet.mappedStacks.put(Material.BREAD, 3);
        MineZRuleSet.mappedStacks.put(Material.GOLDEN_APPLE, 1);
        MineZRuleSet.mappedStacks.put(Material.WEB, 1);
    }

    public MineZRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.BREAD), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.NON_SPEED, false, false);

	    // Enable in duels
	    this.enabledInDuels = false;

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);

        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 2);

        this.defaultInventoryKit[2] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[3] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[4] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[5] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[6] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[7] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[8] = ItemStackUtil.HEALING_POTION_II;

        this.defaultInventoryKit[27] = new ItemStack(Material.ARROW, 15);

        this.defaultInventoryKit[28] = new ItemStack(Material.BOW);

        this.defaultInventoryKit[29] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[30] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[31] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[32] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[33] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[34] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[35] = ItemStackUtil.HEALING_POTION_II;

        this.defaultInventoryKit[18] = new ItemStack(Material.ARROW, 15);
        this.defaultInventoryKit[19] = new ItemStack(Material.ARROW, 15);
        this.defaultInventoryKit[20] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[21] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[22] = ItemStackUtil.HEALING_SPLASH_II;
        this.defaultInventoryKit[23] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[24] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[25] = ItemStackUtil.HEALING_POTION_II;
        this.defaultInventoryKit[26] = ItemStackUtil.HEALING_POTION_II;

	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 15);
        this.defaultInventoryKit[10] = new ItemStack(Material.WEB);
        this.defaultInventoryKit[11] = new ItemStack(Material.WEB);
        this.defaultInventoryKit[12] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[13] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[14] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[15] = new ItemStack(Material.BREAD, 3);
        this.defaultInventoryKit[16] = new ItemStack(Material.BREAD, 3);
        this.defaultInventoryKit[17] = new ItemStack(Material.BREAD, 3);

        // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5MineZ";
	    this.info2Sign[2] = "";
	    this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dIron Armor";
	    this.info4Sign[1] = "FF 4 Boots";
	    this.info4Sign[2] = "";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = "§Diamond Sword";
	    this.info5Sign[1] = "No Enchants";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "2 Cobwebs";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "1x Infinity I";
	    this.info6Sign[2] = "1x Power II";
	    this.info6Sign[3] = "45 Arrows";

	    this.info8Sign[0] = "§dPotions & Food";
	    this.info8Sign[1] = "12DrinkHealthII";
	    this.info8Sign[2] = "9SplashHealthII";
	    this.info8Sign[3] = "12Bread/3GApple";
    }

}
