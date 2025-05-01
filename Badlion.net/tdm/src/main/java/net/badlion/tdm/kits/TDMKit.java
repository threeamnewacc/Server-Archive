package net.badlion.tdm.kits;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.kits.MPGKit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TDMKit extends MPGKit {

    private final static ItemStack[] defaultItems = new ItemStack[36];
    private final static ItemStack[] defaultArmor = new ItemStack[4];

    private final static TDMKit kit = new TDMKit();

    public TDMKit() {
        super(null, "TDM Kit", 0, "", "tdm", null, TDMKit.defaultItems, TDMKit.defaultArmor);

	    // Create armor contents
	    this.armorContents[3] = new ItemStack(Material.LEATHER_HELMET);
	    this.armorContents[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
	    this.armorContents[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.armorContents[0] = new ItemStack(Material.LEATHER_BOOTS);

	    // Create inventory contents
	    this.inventoryContents[0] = new ItemStack(Material.STONE_SWORD);
	    this.inventoryContents[1] = new ItemStack(Material.BOW);
	    this.inventoryContents[2] = new ItemStack(Material.COOKED_BEEF, 64);
	    this.inventoryContents[3] = new ItemStack(Material.GOLDEN_APPLE);
	    this.inventoryContents[8] = new ItemStack(Material.ARROW, 16);

	    // Set preview item manually, not in the superclass's constructor call
        this.previewItem = ItemStackUtil.createItem(Material.LEATHER_CHESTPLATE,
		        ChatColor.GREEN + this.kitName, ChatColor.YELLOW + "Middle click to preview kit");
    }

	public static TDMKit getKit() {
		return TDMKit.kit;
	}

}
