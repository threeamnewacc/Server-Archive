package net.badlion.potpvp.rulesets;

import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ArchonRuleSet extends KitRuleSet {

    public ArchonRuleSet(int id, String name) {
        super(id, name, ArenaManager.ArenaType.NON_PEARL, false, false);

	    // Create default armor kit
	    this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
	    this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);

	    // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 64);
        this.defaultInventoryKit[8] = new ItemStack(Material.COOKED_BEEF, 15);

	    // Initialize info signs
	    this.info2Sign[0] = "================";
	    this.info2Sign[1] = "    §5Archon";
	    this.info2Sign[2] = "";
		this.info2Sign[3] = "================";

	    this.info4Sign[0] = "    §dIron Armor";
	    this.info4Sign[1] = "   No Enchants";
	    this.info4Sign[2] = "";
	    this.info4Sign[3] = "";

	    this.info5Sign[0] = " §dDiamond Sword";
	    this.info5Sign[1] = "  No Enchants";
	    this.info5Sign[2] = "";
	    this.info5Sign[3] = "";

	    this.info6Sign[0] = "     §dBow";
	    this.info6Sign[1] = " No Enchants";
	    this.info6Sign[2] = "  15 Arrows";
	    this.info6Sign[3] = "";

	    this.info8Sign[0] = "     §dFood";
	    this.info8Sign[1] = "  64 Steak";
	    this.info8Sign[2] = "";
	    this.info8Sign[3] = "";
    }

}
