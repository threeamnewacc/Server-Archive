package net.badlion.skywars.gamemodes;

import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.skywars.kits.ClassicDefaultKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.*;

public class ClassicGamemode extends Gamemode implements Listener {

    public static final int NUM_OF_TIER_GUARANTEED = 4;
    public static final int NUM_OF_TIER_RANDOM = 3;

    private static Random random = new Random();

    /**
     * Get a random item
     */
    public ItemStack getTierItem(int tier) {
        switch(tier) {
            case 1:
                return getTier1Item();
            case 2:
                return getTier2Item();
            default:
                return getTier1Item();
        }
    }

    /**
     * Get a Tier 1 Item
     */
    private ItemStack getTier1Item() {
        int rarity = random.nextInt(100);

        if (rarity < 60) {
            int i = random.nextInt(27);
            switch (i) {
                case 0:
                    return new ItemStack(Material.WOOD, random.nextInt(9) + 24, (short) 0);
                case 1:
                    return new ItemStack(Material.WOOD, random.nextInt(9) + 24, (short) 3);
                case 2:
                    return new ItemStack(Material.WOOD, random.nextInt(9) + 24, (short) 4);
                case 3:
                    return new ItemStack(Material.WOOD, random.nextInt(9) + 24, (short) 2);
                case 4:
                    return new ItemStack(Material.COBBLESTONE, random.nextInt(9) + 24);
                case 5:
                    return new ItemStack(Material.SNOW_BALL, random.nextInt(9) + 8);
                case 6:
                    return new ItemStack(Material.EGG, random.nextInt(9) + 8);
                case 7:
                    return new ItemStack(Material.LEATHER_HELMET);
                case 8:
                    return new ItemStack(Material.LEATHER_CHESTPLATE);
                case 9:
                    return new ItemStack(Material.LEATHER_LEGGINGS);
                case 10:
                    return new ItemStack(Material.LEATHER_BOOTS);
                case 11:
                    return new ItemStack(Material.FISHING_ROD);
                case 12:
                    return new ItemStack(Material.WOOD_SWORD);
                case 13:
                    return new ItemStack(Material.WOOD_AXE);
                case 14:
                    return new ItemStack(Material.WOOD_SPADE);
                case 15:
                    return new ItemStack(Material.GOLD_AXE);
                case 16:
                    return new ItemStack(Material.GOLD_SPADE);
                case 17:
                    return new ItemStack(Material.GOLD_SWORD);
                case 18:
                    return new ItemStack(Material.BOW);
                case 19:
                    return new ItemStack(Material.ARROW, random.nextInt(5) + 4);
                case 20:
                    return new ItemStack(Material.STONE, random.nextInt(9) + 24);
                case 21:
                    return new ItemStack(Material.COOKED_FISH, random.nextInt(2) + 2);
                case 22:
                    return new ItemStack(Material.COOKIE, random.nextInt(2) + 2);
                case 23:
                    return new ItemStack(Material.CARROT_ITEM, random.nextInt(2) + 2);
                case 24:
                    return new ItemStack(Material.APPLE, random.nextInt(2) + 2);
                case 25:
                    return new ItemStack(Material.MELON, random.nextInt(2) + 2);
                case 26:
                    return new ItemStack(Material.BREAD, random.nextInt(2) + 2);
            }
        } else if (rarity < 95) {
            int i = random.nextInt(18);
            switch (i) {
                case 0:
                    return new ItemStack(Material.WATER_BUCKET);
                case 1:
                    return new ItemStack(Material.ARROW, random.nextInt(9) + 4);
                case 2:
                    return new ItemStack(Material.WEB);
                case 3:
                    return new ItemStack(Material.STONE_SWORD);
                case 4:
                    return new ItemStack(Material.STONE_AXE);
                case 5:
                    return new ItemStack(Material.STONE_PICKAXE);
                case 6:
                    return new ItemStack(Material.GOLD_HELMET);
                case 7:
                    return new ItemStack(Material.GOLD_CHESTPLATE);
                case 8:
                    return new ItemStack(Material.GOLD_LEGGINGS);
                case 9:
                    return new ItemStack(Material.EXP_BOTTLE, 2);
                case 10:
                    return new ItemStack(Material.BAKED_POTATO, random.nextInt(2) + 2);
                case 11:
                    return new ItemStack(Material.COOKED_CHICKEN, random.nextInt(2) + 2);
                case 12:
                    return new ItemStack(Material.COOKED_BEEF, random.nextInt(2) + 2);
                case 13:
                    return new ItemStack(Material.GOLDEN_CARROT, random.nextInt(2) + 2);
                case 14:
                    return new ItemStack(Material.GRILLED_PORK, random.nextInt(2) + 2);
                case 15:
                    return new ItemStack(Material.PUMPKIN_PIE, random.nextInt(2) + 2);
                case 16:
                    return new ItemStack(Material.BOW);
                case 17:
                    return new ItemStack(Material.FISHING_ROD);
            }
        } else {
            int i = random.nextInt(3);
            switch (i) {
                case 0:
                    return new ItemStack(Material.POTION, 1, (short) 8261);
                case 1:
                    return new ItemStack(Material.EXP_BOTTLE, 4);
                case 2:
                    return new ItemStack(Material.FLINT_AND_STEEL);
            }
        }

        return null;
    }

    /**
     * Get a Tier 2 Item
     */
    public ItemStack getTier2Item() {
        int rarity = random.nextInt(100);

        if (rarity < 50) {
            int i = random.nextInt(18);
            switch (i) {
                case 0:
                    return new ItemStack(Material.CHAINMAIL_BOOTS);
                case 1:
                    return new ItemStack(Material.CHAINMAIL_CHESTPLATE);
                case 2:
                    return new ItemStack(Material.CHAINMAIL_HELMET);
                case 3:
                    return new ItemStack(Material.CHAINMAIL_LEGGINGS);
                case 4:
                    return new ItemStack(Material.ARROW, random.nextInt(9) + 8);
                case 5:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(2) + 2);
                case 6:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(4) + 2);
                case 7:
                    return new ItemStack(Material.ENDER_PEARL, 1);
                case 8:
                    return new ItemStack(Material.BOW);
                case 9:
                    return new ItemStack(Material.FISHING_ROD);
                case 10:
                    ItemStack item = new ItemStack(Material.GOLD_SWORD);
                    item.addEnchantment(Enchantment.DURABILITY, 3);
                    item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    return item;
                case 11:
                    return new ItemStack(Material.DIAMOND, 1);
                case 12:
                    return new ItemStack(Material.IRON_INGOT, 2);
                case 13:
                    return new ItemStack(Material.SNOW_BALL, random.nextInt(5) + 12);
                case 14:
                    return new ItemStack(Material.EGG, random.nextInt(5) + 12);
                case 15:
                    return new ItemStack(Material.GOLDEN_APPLE, 1);
                case 16:
                    return new ItemStack(Material.WEB);
                case 17:
                    return new ItemStack(Material.WATER_BUCKET);
            }
        } else if (rarity < 90) {
            int i = random.nextInt(25);
            switch (i) {
                case 0:
                    return new ItemStack(Material.IRON_HELMET);
                case 1:
                    return new ItemStack(Material.IRON_CHESTPLATE);
                case 2:
                    return new ItemStack(Material.IRON_LEGGINGS);
                case 3:
                    return new ItemStack(Material.IRON_BOOTS);
                case 4:
                    return new ItemStack(Material.GOLDEN_APPLE, 2);
                case 5:
                    return new ItemStack(Material.IRON_INGOT, 3);
                case 6:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(2) + 6);
                case 7:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(2) + 8);
                case 8:
                    return new ItemStack(Material.DIAMOND, 2);
                case 9:
                    return new ItemStack(Material.ENDER_PEARL, 2);
                case 10:
                    return new ItemStack(Material.ARROW, random.nextInt(9) + 16);
                case 11:
                    return new ItemStack(Material.POTION, 1, (short) 16453);
                case 12:
                    return new ItemStack(Material.POTION, 1, (short) 16386);
                case 13:
                    return new ItemStack(Material.POTION, 1, (short) 16426);
                case 14:
                    return new ItemStack(Material.POTION, 1, (short) 16385);
                case 15:
                    return new ItemStack(Material.POTION, 1, (short) 16451);
                case 16:
                    return new ItemStack(Material.POTION, 1, (short) 16388);
                case 17:
                    ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) item.getItemMeta();
                    itemMeta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, false);
                    item.setItemMeta(itemMeta);
                    return item;
                case 18:
                    return new ItemStack(Material.IRON_SWORD);
                case 19:
                    ItemStack item2 = new ItemStack(Material.BOW);
                    item2.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                    return item2;
                case 20:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 51);
                case 21:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 50);
                case 22:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 95);
                case 23:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 59);
                case 24:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 57);
            }
        } else {
            int i = random.nextInt(12);
            switch (i) {
                case 0:
                    return new ItemStack(Material.POTION, 1, (short) 16421);
                case 1:
                    ItemStack item2 = new ItemStack(Material.BOW);
                    item2.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                    return item2;
                case 2:
                    return new ItemStack(Material.DIAMOND, 3);
                case 3:
                    return new ItemStack(Material.TNT);
                case 4:
                    ItemStack item3 = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) item3.getItemMeta();
                    itemMeta.addStoredEnchant(Enchantment.ARROW_FIRE, 1, false);
                    item3.setItemMeta(itemMeta);
                    return item3;
                case 5:
                    ItemStack item4 = new ItemStack(Material.ENCHANTED_BOOK);
                    EnchantmentStorageMeta itemMeta2 = (EnchantmentStorageMeta) item4.getItemMeta();
                    itemMeta2.addStoredEnchant(Enchantment.FIRE_ASPECT, 1, false);
                    item4.setItemMeta(itemMeta2);
                    return item4;
                case 6:
                    ItemStack item5 = new ItemStack(Material.IRON_SWORD);
                    item5.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    return item5;
                case 7:
                    return new ItemStack(Material.ENDER_PEARL, random.nextInt(8) + 3);
                case 8:
                    return new ItemStack(Material.LAVA_BUCKET);
                case 9:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(2) + 10);
                case 10:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(2) + 12);
                case 11:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 61);
            }
        }

        return null;
    }

    @Override
    public List<ItemStack> getCommonTierItems(int tier) {
        return null;
    }

    /**
     * Get number of random items
     */
    public int getNumOfTierRandom(int tier) {
        return ClassicGamemode.NUM_OF_TIER_RANDOM;
    }

    /**
     * Get number of guaranteed items
     */
    public int getNumOfTierGuaranteed(int tier) {
        return ClassicGamemode.NUM_OF_TIER_GUARANTEED;
    }

    /**
     * Do anything special for a death
     * @param died
     */
    public void handleDeath(LivingEntity died) {
        // Do nothing special
    }

    /**
     * Return name
     */
    public String getName() {
        return "Classic";
    }

    /**
     * Return default kit
     */
    public MPGKit getDefaultKit() {
        return ClassicDefaultKit.getKit();
    }

}