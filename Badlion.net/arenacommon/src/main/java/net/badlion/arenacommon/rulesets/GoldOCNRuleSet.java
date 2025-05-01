package net.badlion.arenacommon.rulesets;

import net.badlion.arenacommon.ArenaCommon;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class GoldOCNRuleSet extends KitRuleSet {

    public GoldOCNRuleSet(int id, String name) {
        super(id, name, new ItemStack(Material.GOLD_HELMET),  ArenaCommon.ArenaType.NON_PEARL, KnockbackType.NON_SPEED, false, false);

        // Create default armor kit
        this.defaultArmorKit[3] = new ItemStack(Material.GOLD_HELMET);
        this.defaultArmorKit[3].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        this.defaultArmorKit[2] = new ItemStack(Material.GOLD_CHESTPLATE);
        this.defaultArmorKit[2].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        this.defaultArmorKit[1] = new ItemStack(Material.GOLD_LEGGINGS);
        this.defaultArmorKit[1].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        this.defaultArmorKit[0] = new ItemStack(Material.GOLD_BOOTS);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
        this.defaultArmorKit[0].addEnchantment(Enchantment.PROTECTION_FALL, 4);

        // Create default inventory kit
        this.defaultInventoryKit[0] = new ItemStack(Material.GOLD_SWORD);
        this.defaultInventoryKit[0].addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        this.defaultInventoryKit[1] = new ItemStack(Material.BOW);
        this.defaultInventoryKit[2] = new ItemStack(Material.GOLDEN_APPLE, 2);
        this.defaultInventoryKit[3] = new ItemStack(Material.COOKED_BEEF, 8);
        this.defaultInventoryKit[8] = new ItemStack(Material.ARROW, 8);

        // Initialize info signs
        this.info2Sign[0] = "================";
        this.info2Sign[1] = "§5Gold";
        this.info2Sign[2] = "";
        this.info2Sign[3] = "================";

        this.info4Sign[0] = "§dGold Armor";
        this.info4Sign[1] = "Prot I";
        this.info4Sign[2] = "Feather";
        this.info4Sign[3] = "Falling IV";

        this.info5Sign[0] = "§dGold Sword";
        this.info5Sign[1] = "Unbreaking X";
        this.info5Sign[2] = "";
        this.info5Sign[3] = "";

        this.info8Sign[0] = "§dFood";
        this.info8Sign[1] = "2 Golden";
        this.info8Sign[2] = "Apples";
        this.info8Sign[3] = "";
    }

}
