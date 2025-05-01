package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.util.ItemStackUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class AdvancedUHCRuleSet extends KitRuleSet {

    public AdvancedUHCRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.SKULL_ITEM, 1, (short) 3), ArenaCommon.ArenaType.NON_PEARL, KnockbackType.NON_SPEED, false, false);

	    // Enable in duels
	    this.enabledInDuels = false;

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.DIAMOND_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        this.defaultArmorKit[2] = new ItemStack(Material.IRON_CHESTPLATE);
        this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

        this.defaultArmorKit[1] = new ItemStack(Material.IRON_LEGGINGS);
        this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);

        this.defaultArmorKit[0] = new ItemStack(Material.IRON_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);

        // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.IRON_SWORD);
        this.defaultInventoryKit[0].addEnchantment(Enchantment.DAMAGE_ALL, 2);

        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[1].addEnchantment(Enchantment.ARROW_DAMAGE, 2);

        this.defaultInventoryKit[2] = new ItemStack(Material.COOKED_BEEF, 16);
        this.defaultInventoryKit[3] = ItemStackUtil.GOLDEN_APPLE;
        this.defaultInventoryKit[4] = ItemStackUtil.createGoldenHead();
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 64);

        // Initialize info signs
        this.info2Sign[0] = "================";
        this.info2Sign[1] = "§5Advance UHC";
        this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

        this.info4Sign[0] = "§dIron Armor";
        this.info4Sign[1] = "ProtIIHelm/Legs";
        this.info4Sign[2] = "Proj Prot II";
        this.info4Sign[3] = "Chest/Boots";

        this.info5Sign[0] = "§dIron Sword";
        this.info5Sign[1] = "Sharpness II";
        this.info5Sign[2] = "";
        this.info5Sign[3] = "";

        this.info6Sign[0] = "§dBow";
        this.info6Sign[1] = "Power II";
        this.info6Sign[2] = "";
        this.info6Sign[3] = "";

        this.info8Sign[0] = "§dFood";
        this.info8Sign[1] = "16 Steak";
        this.info8Sign[2] = "1 Gold Apple";
        this.info8Sign[3] = "1 Gold Head";
    }
}
