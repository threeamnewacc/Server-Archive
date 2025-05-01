package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ComboBuildUHCRuleSet extends KitRuleSet {

	private List<Material> whitelistedBlocks = new ArrayList<>();

    public ComboBuildUHCRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.CAKE), ArenaCommon.ArenaType.BUILD_UHC, KnockbackType.NON_SPEED, false, false);

//      Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.OneVsOneRanked, true, true));
//		Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.TwoVsTwoRanked, true, true));
//		Ladder.registerLadder(name, new MatchLadder(id, this, new QueueService(2), Ladder.LadderType.ThreeVsThreeRanked, true, true));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
	    this.defaultArmorKit[3].addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

	    this.defaultArmorKit[2] = new ItemStack(Material.DIAMOND_CHESTPLATE);
	    this.defaultArmorKit[2].addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);

	    this.defaultArmorKit[1] = new ItemStack(Material.DIAMOND_LEGGINGS);
	    this.defaultArmorKit[1].addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);

	    this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.GOLD_SWORD);
	    this.defaultInventoryKit[0].addUnsafeEnchantment(Enchantment.DURABILITY, 3);

	    this.defaultInventoryKit[1] = new ItemStack(Material.FISHING_ROD);
	    this.defaultInventoryKit[1].addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);

	    this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[2].addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
	    this.defaultInventoryKit[2].addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);

	    this.defaultInventoryKit[3] = new ItemStack(Material.COOKED_BEEF, 64);
	    this.defaultInventoryKit[4] = new ItemStack(Material.GOLDEN_APPLE, 18);
	    this.defaultInventoryKit[5] = ItemStackUtil.createGoldenHead(9);
	    this.defaultInventoryKit[6] = new ItemStack(Material.DIAMOND_PICKAXE);
	    this.defaultInventoryKit[7] = new ItemStack(Material.DIAMOND_AXE);
	    this.defaultInventoryKit[8] = new ItemStack(Material.WOOD, 64);
	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW);
	    this.defaultInventoryKit[10] = new ItemStack(Material.COBBLESTONE, 64);
	    this.defaultInventoryKit[11] = new ItemStack(Material.WATER_BUCKET);
	    this.defaultInventoryKit[12] = new ItemStack(Material.WATER_BUCKET);
	    this.defaultInventoryKit[13] = new ItemStack(Material.LAVA_BUCKET);
	    this.defaultInventoryKit[14] = new ItemStack(Material.LAVA_BUCKET);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5COMBO BuildUHC";
	    this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dDiamond Armor";
	    this.info4Sign[1] = "Prot2Chest/Legs";
	    this.info4Sign[2] = "Proj Prot II";
	    this.info4Sign[3] = "Helm/Boots";

	    this.info5Sign[0] = "§dDiamond Sword";
	    this.info5Sign[1] = "Sharpness III";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "Power III";
	    this.info6Sign[2] = "64 Arrows";
	    this.info6Sign[3] = "2 Water & Lava";

		this.info7Sign[0] = "§dOther";
		this.info7Sign[1] = "D PickAxe";
		this.info7Sign[2] = "D Axe";
		this.info7Sign[3] = "Fishing Rod";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Steak";
	    this.info8Sign[2] = "18 Gold Apples";
	    this.info8Sign[3] = "9 Gold Heads";

		this.info9Sign[0] = "§dBlocks";
		this.info9Sign[1] = "64 Cobble";
		this.info9Sign[2] = "64 Planks";
		this.info9Sign[3] = "";

	    this.whitelistedBlocks.add(Material.LOG);
	    this.whitelistedBlocks.add(Material.LOG_2);
	    this.whitelistedBlocks.add(Material.WOOD);
	    this.whitelistedBlocks.add(Material.LEAVES);
	    this.whitelistedBlocks.add(Material.LEAVES_2);
	    this.whitelistedBlocks.add(Material.WATER);
	    this.whitelistedBlocks.add(Material.STATIONARY_WATER);
	    this.whitelistedBlocks.add(Material.LAVA);
	    this.whitelistedBlocks.add(Material.STATIONARY_LAVA);
	    this.whitelistedBlocks.add(Material.LONG_GRASS);
	    this.whitelistedBlocks.add(Material.YELLOW_FLOWER);
	    this.whitelistedBlocks.add(Material.COBBLESTONE);
	    this.whitelistedBlocks.add(Material.CACTUS);
	    this.whitelistedBlocks.add(Material.SUGAR_CANE_BLOCK);
	    this.whitelistedBlocks.add(Material.OBSIDIAN);
	    this.whitelistedBlocks.add(Material.SNOW);
    }

}
