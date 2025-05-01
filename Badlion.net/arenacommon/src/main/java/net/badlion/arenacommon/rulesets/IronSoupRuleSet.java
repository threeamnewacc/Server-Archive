package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class IronSoupRuleSet extends SoupRuleSet {

    public IronSoupRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.MUSHROOM_SOUP), KnockbackType.SPEED_II);

        // Enable in duels
        this.enabledInDuels = true;

        // Create TDM kill reward items
        this.tdmKillRewardItems.add(ItemStackUtil.MUSHROOM_SOUP);
        this.tdmKillRewardItems.add(ItemStackUtil.MUSHROOM_SOUP);
        this.tdmKillRewardItems.add(ItemStackUtil.MUSHROOM_SOUP);
        this.tdmKillRewardItems.add(ItemStackUtil.MUSHROOM_SOUP);

        // Initialize FFAWorld for this rule set
        //ItemStack ffaItem = ItemStackUtil.createItem(Material.MUSHROOM_SOUP, ChatColor.GREEN + "Join Soup FFA", ChatColor.YELLOW + "Players: 0");

        //SoupFFAWorld ffa = new SoupFFAWorld(ffaItem, this);
        //PotPvP.getInstance().getServer().getPluginManager().registerEvents(ffa, PotPvP.getInstance());

        // Speed 2
        this.potionEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
	    this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
	    this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
	    this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);

	    // Create default inventory kit
	    this.defaultInventoryKit[0] = new ItemStack(Material.DIAMOND_SWORD);
	    this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 3);
	    for (int i = 1; i < 36; i++ ) {
		    this.defaultInventoryKit[i] = new ItemStack(Material.MUSHROOM_SOUP);
	    }

        // Initialize info signs
        this.info2Sign[0] = "================";
        this.info2Sign[1] = "§5Iron";
        this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

        this.info4Sign[0] = "§dIron Armor";
        this.info4Sign[1] = "";
        this.info4Sign[2] = "";
        this.info4Sign[3] = "";

        this.info5Sign[0] = "§dDiamond Sword";
        this.info5Sign[1] = "Sharp 1";
        this.info5Sign[2] = "";
        this.info5Sign[3] = "";

        this.info6Sign[0] = "§dSoups";
        this.info6Sign[1] = "35 Soups";
        this.info6Sign[2] = "";
        this.info6Sign[3] = "";
    }

}
