package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class IronBuildUHCRuleSet extends KitRuleSet {

	public static List<Material> whitelistedBlocks = new ArrayList<>();
	//private static Map<UUID, Deque<Location>> blockLocations = new HashMap<>();

	static {
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.LOG);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.LOG_2);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.WOOD);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.LEAVES);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.LEAVES_2);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.WATER);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.STATIONARY_WATER);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.LAVA);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.STATIONARY_LAVA);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.LONG_GRASS);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.YELLOW_FLOWER);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.COBBLESTONE);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.CACTUS);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.SUGAR_CANE_BLOCK);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.OBSIDIAN);
		IronBuildUHCRuleSet.whitelistedBlocks.add(Material.SNOW);
	}

    public IronBuildUHCRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.WATER_BUCKET), ArenaCommon.ArenaType.BUILD_UHC, KnockbackType.NON_SPEED, false, false);

	    // Enable in duels
	    this.enabledInDuels = false;

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
	    this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    this.defaultArmorKit[0] = new ItemStack(Material.DIAMOND_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 1);

	    this.defaultInventoryKit[1] = new ItemStack(Material.FISHING_ROD);

	    this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[2].addEnchantment(Enchantment.ARROW_DAMAGE, 1);

	    this.defaultInventoryKit[3] = new ItemStack(Material.COOKED_BEEF, 64);
	    this.defaultInventoryKit[4] = new ItemStack(Material.GOLDEN_APPLE, 4);
	    this.defaultInventoryKit[5] = ItemStackUtil.createGoldenHead(2);
	    this.defaultInventoryKit[6] = new ItemStack(Material.DIAMOND_PICKAXE);
	    this.defaultInventoryKit[7] = new ItemStack(Material.WATER_BUCKET);
	    this.defaultInventoryKit[8] = new ItemStack(Material.COBBLESTONE, 64);
	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 20);
	    this.defaultInventoryKit[10] = new ItemStack(Material.COBBLESTONE, 64);
	    this.defaultInventoryKit[11] = new ItemStack(Material.WATER_BUCKET);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5Iron Build UHC";
	    this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dIron Armor";
	    this.info4Sign[1] = "Prot 2 H/C/L";
	    this.info4Sign[2] = "Proj Prot 3";
	    this.info4Sign[3] = "Boots";

	    this.info5Sign[0] = "§Diamond Sword";
	    this.info5Sign[1] = "Sharpness I";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "§dBow";
	    this.info6Sign[1] = "Power I";
	    this.info6Sign[2] = "20 Arrows";
	    this.info6Sign[3] = "2 Water Buckets";

		this.info7Sign[0] = "§dOther";
		this.info7Sign[1] = "D Pickaxe";
		this.info7Sign[2] = "";
		this.info7Sign[3] = "Fishing Rod";

	    this.info8Sign[0] = "§dFood";
	    this.info8Sign[1] = "64 Steak";
	    this.info8Sign[2] = "4 Gold Apples";
	    this.info8Sign[3] = "2 Gold Heads";

		this.info9Sign[0] = "§dBlocks";
		this.info9Sign[1] = "128 Cobble";
		this.info9Sign[2] = "";
		this.info9Sign[3] = "";

    }

	@Override
	public void sendMessages(Player player) {
		player.sendMessage(ChatColor.DARK_RED + "WARNING: " + ChatColor.DARK_AQUA + "If you sky base camp you will " +
							   "receive a punishment (ArenaPvP Rule 6).");
	}

}
