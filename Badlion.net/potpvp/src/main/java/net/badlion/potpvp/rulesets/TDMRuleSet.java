package net.badlion.potpvp.rulesets;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.tdm.TDMGame;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TDMRuleSet extends KitRuleSet {

    public TDMRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.LEATHER_CHESTPLATE), ArenaManager.ArenaType.NON_PEARL, false, false);

	    // Create TDM kill reward items
	    TDMGame.getKitRuleSets().add(this);
	    this.tdmKillRewardItems.add(ItemStackUtil.GOLDEN_APPLE);
	    this.tdmKillRewardItems.add(new ItemStack(Material.ARROW, 10));

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.LEATHER_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.LEATHER_BOOTS);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.STONE_SWORD);
	    this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
	    this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 64);
	    this.defaultInventoryKit[3] = ItemStackUtil.GOLDEN_APPLE;
	    this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 16);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "§TDM Kit";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "§dArmor";
	    this.info4Sign[1] = "Leather Helmet";
	    this.info4Sign[2] = "Leather Chest/Boots";
	    this.info4Sign[3] = "Iron Leggings";

	    this.info5Sign[0] = "§dWeapons";
	    this.info5Sign[1] = "Stone Sword";
	    this.info5Sign[2] = "Bow - 16 Arrows";

	    this.info6Sign[0] = "§dFood";
	    this.info6Sign[1] = "64 Steak";
    }

}
