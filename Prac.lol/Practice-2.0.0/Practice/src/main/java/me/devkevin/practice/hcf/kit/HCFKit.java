package me.devkevin.practice.hcf.kit;

import me.devkevin.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 25/02/2023 @ 0:12
 * HCFKit / me.devkevin.practice.hcf.kit / Practice
 */
public class HCFKit {

    public final ItemStack fireRes = new ItemStack(Material.POTION, 1, (short) 8259);

    public void giveBardKit(Player player) {
        ItemStack helmet = new ItemStack(Material.GOLD_HELMET, 1);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 5);

        ItemStack chestplate = new ItemStack(Material.GOLD_CHESTPLATE, 1);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        chestplate.addUnsafeEnchantment(Enchantment.DURABILITY, 5);

        ItemStack pants = new ItemStack(Material.GOLD_LEGGINGS, 1);
        pants.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        pants.addUnsafeEnchantment(Enchantment.DURABILITY, 5);

        ItemStack boots = new ItemStack(Material.GOLD_BOOTS, 1);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        boots.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);

        PlayerInventory inventory = player.getInventory();
        giveSword(player, inventory);
        inventory.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
        inventory.setItem(2, new ItemStack(Material.BLAZE_POWDER, 32));
        inventory.setItem(3, new ItemStack(Material.SUGAR, 32));
        //inventory.setItem(7, fireRes);
        inventory.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        inventory.setItem(9, new ItemStack(Material.IRON_INGOT, 32));
        inventory.setItem(10, new ItemStack(Material.GHAST_TEAR, 32));
        inventory.setItem(18, new ItemStack(Material.FEATHER, 32));
        inventory.setItem(19, new ItemStack(Material.MAGMA_CREAM, 32));
        giveHealing(player);

        inventory.setHelmet(helmet);
        inventory.setChestplate(chestplate);
        inventory.setLeggings(pants);
        inventory.setBoots(boots);

        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
    }

    public void giveArcherKit(Player player) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        helmet.addUnsafeEnchantment(Enchantment.DURABILITY, 5);

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        chestplate.addUnsafeEnchantment(Enchantment.DURABILITY, 5);

        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        pants.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        pants.addUnsafeEnchantment(Enchantment.DURABILITY, 5);

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        boots.addUnsafeEnchantment(Enchantment.DURABILITY, 5);
        boots.addEnchantment(Enchantment.PROTECTION_FALL, 4);

        ItemStack bow = new ItemStack(Material.BOW, 1);
        bow.addEnchantment(Enchantment.ARROW_DAMAGE, 5);
        bow.addEnchantment(Enchantment.ARROW_FIRE, 1);
        bow.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        bow.addEnchantment(Enchantment.DURABILITY, 3);

        PlayerInventory inventory = player.getInventory();
        giveSword(player, inventory);
        inventory.setItem(1, new ItemStack(Material.ENDER_PEARL, 16));
        inventory.setItem(2, bow);
        //inventory.setItem(3, fireRes);
        inventory.setItem(7, new ItemStack(Material.SUGAR, 32));
        inventory.setItem(8, new ItemStack(Material.BAKED_POTATO, 64));
        inventory.setItem(9, new ItemStack(Material.ARROW, 1));
        giveHealing(player);

        inventory.setHelmet(helmet);
        inventory.setChestplate(chestplate);
        inventory.setLeggings(pants);
        inventory.setBoots(boots);

        player.updateInventory();

        // give auto potion effects
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));
    }

    private void giveSword(Player player, Inventory inventory) {
        boolean hasSword = false;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.DIAMOND_SWORD) {
                hasSword = true;
            }
        }

        if (!hasSword) {
            inventory.setItem(0, new ItemBuilder(Material.DIAMOND_SWORD).enchantment(Enchantment.DAMAGE_ALL, 1).build());
        }
    }

    private void giveHealing(Player player) {
        final ItemStack heal = new ItemStack(Material.POTION, 1, (short) 16421);
        for (ItemStack inv : player.getInventory().getContents()) {
            if (inv == null) {
                player.getInventory().addItem(heal);
            }
        }
    }
}
