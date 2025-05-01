package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SGRuleSet extends KitRuleSet {

    public SGRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.FISHING_ROD), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.NON_SPEED, false, false);

	    // Enable in duels
	    this.enabledInDuels = true;

	    // Initialize FFAWorld for this rule set
	    ItemStack ffaItem = ItemStackUtil.createItem(Material.FISHING_ROD, ChatColor.GREEN + "Join SG FFA", ChatColor.YELLOW + "Players: 0");

	    //SGFFAWorld ffa = new SGFFAWorld(ffaItem, this);
	   	// PotPvP.getInstance().getServer().getPluginManager().registerEvents(ffa, PotPvP.getInstance());

	    // Create TDM kill reward items
	    //TDMGame.getKitRuleSets().add(this);
	    this.tdmKillRewardItems.add(ItemStackUtil.GOLDEN_APPLE);
	    this.tdmKillRewardItems.add(new ItemStack(Material.ARROW, 16));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.GOLD_HELMET);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[3]);
	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[2]);
	    this.defaultArmorKit[1] = new ItemStack(Material.CHAINMAIL_LEGGINGS);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[1]);
	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
	    ItemStackUtil.addUnbreaking(this.defaultArmorKit[0]);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.STONE_SWORD);
	    this.defaultInventoryKit[1] = new ItemStack(Material.FISHING_ROD);
	    this.defaultInventoryKit[2] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[3] = new ItemStack(Material.GOLDEN_APPLE);
	    this.defaultInventoryKit[4] = new ItemStack(Material.GOLDEN_CARROT);
	    this.defaultInventoryKit[5] = new ItemStack(Material.PUMPKIN_PIE, 2);
	    this.defaultInventoryKit[6] = new ItemStack(Material.MELON, 2);
	    this.defaultInventoryKit[7] = new ItemStack(Material.BREAD);
        this.defaultInventoryKit[8] = new ItemStack(Material.FLINT_AND_STEEL);
	    this.defaultInventoryKit[9] = new ItemStack(Material.ARROW, 8);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§5SG";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dArmor";
	    this.info4Sign[1] = "Gold Helmet";
	    this.info4Sign[2] = "Iron Chest/Boots";
	    this.info4Sign[3] = "Chain Leggings";

	    this.info5Sign[0] = "§dWeapons";
	    this.info5Sign[1] = "Stone Sword";
	    this.info5Sign[2] = "Fishing Rod";
	    this.info5Sign[3] = "Bow - 8 Arrows";

	    this.info6Sign[0] = "§dFood";
	    this.info6Sign[1] = "1 Golden Carrot";
	    this.info6Sign[2] = "2 Melon/Pie";
	    this.info6Sign[3] = "1 Bread";
    }

}
