package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HorseRuleSet extends KitRuleSet {

	private Set<UUID> quittingPlayers = new HashSet<>();

    public HorseRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.GOLD_BARDING),  ArenaCommon.ArenaType.HORSE, KnockbackType.NON_SPEED, false, false);

	    // Enable in duels
	    this.enabledInDuels = true;

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.IRON_HELMET);
        this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
        this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
        this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

        // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.IRON_SWORD);
        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 64);
        this.defaultInventoryKit[3] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[4] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[7] = new ItemStack(Material.ARROW, 64);
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 64);

        // Initialize info signs
        this.info2Sign[0] = "================";
        this.info2Sign[1] = "§5Horse";
        this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

        this.info4Sign[0] = "§dIron Armor";
        this.info4Sign[1] = "Feather";
        this.info4Sign[2] = "Falling IV";
        this.info4Sign[3] = "";

        this.info5Sign[0] = "§dIron Sword";
        this.info5Sign[1] = "No Enchants";
        this.info5Sign[2] = "";
        this.info5Sign[3] = "";

        this.info6Sign[0] = "§dBow";
        this.info6Sign[1] = "No Enchants";
        this.info6Sign[2] = "128 Arrows";
        this.info6Sign[3] = "";

        this.info8Sign[0] = "§dFood";
        this.info8Sign[1] = "64 Steak";
        this.info8Sign[2] = "2 Golden Apples";
        this.info8Sign[3] = "";
    }

}
