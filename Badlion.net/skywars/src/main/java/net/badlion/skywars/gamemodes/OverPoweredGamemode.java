package net.badlion.skywars.gamemodes;

import net.badlion.mpg.gamemodes.Gamemode;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.skywars.kits.OPDefaultKit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class OverPoweredGamemode extends Gamemode implements Listener {

    public static final int NUM_OF_TIER_1_GUARANTEED = 5;
    public static final int NUM_OF_TIER_2_GUARANTEED = 5;
    public static final int NUM_OF_TIER_1_RANDOM = 3;
    public static final int NUM_OF_TIER_2_RANDOM = 3;

    private Random random = new Random();

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
    public ItemStack getTier1Item() {
        int rarity = random.nextInt(100);

        if (rarity < 60) {
            int i = random.nextInt(24);
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
                    return new ItemStack(Material.IRON_HELMET);
                case 8:
                    return new ItemStack(Material.IRON_CHESTPLATE);
                case 9:
                    return new ItemStack(Material.IRON_LEGGINGS);
                case 10:
                    return new ItemStack(Material.IRON_BOOTS);
                case 11:
                    return new ItemStack(Material.FISHING_ROD);
                case 12:
                    return new ItemStack(Material.IRON_SWORD);
                case 13:
                    return new ItemStack(Material.IRON_AXE);
                case 14:
                    return new ItemStack(Material.IRON_SPADE);
                case 15:
                    return new ItemStack(Material.BOW);
                case 16:
                    return new ItemStack(Material.ARROW, random.nextInt(5) + 4);
                case 17:
                    return new ItemStack(Material.STONE, random.nextInt(9) + 24);
                case 18:
                    return new ItemStack(Material.COOKED_FISH, random.nextInt(2) + 2);
                case 19:
                    return new ItemStack(Material.COOKIE, random.nextInt(2) + 2);
                case 20:
                    return new ItemStack(Material.CARROT_ITEM, random.nextInt(2) + 2);
                case 21:
                    return new ItemStack(Material.APPLE, random.nextInt(2) + 2);
                case 22:
                    return new ItemStack(Material.MELON, random.nextInt(2) + 2);
                case 23:
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
                    return new ItemStack(Material.DIAMOND_SWORD);
                case 4:
                    return new ItemStack(Material.DIAMOND_AXE);
                case 5:
                    return new ItemStack(Material.DIAMOND_PICKAXE);
                case 6:
                    return new ItemStack(Material.DIAMOND_HELMET);
                case 7:
                    return new ItemStack(Material.DIAMOND_CHESTPLATE);
                case 8:
                    return new ItemStack(Material.DIAMOND_LEGGINGS);
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

        if (rarity < 30) {
            int i = random.nextInt(17);
            switch (i) {
                case 0:
                    ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
                    item.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    return item;
                case 1:
                    ItemStack item2 = new ItemStack(Material.DIAMOND_HELMET);
                    item2.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    return item2;
                case 2:
                    ItemStack item3 = new ItemStack(Material.DIAMOND_LEGGINGS);
                    item3.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    return item3;
                case 3:
                    ItemStack item4 = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    item4.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                    return item4;
                case 4:
                    return new ItemStack(Material.ARROW, random.nextInt(9) + 16);
                case 5:
                    return new ItemStack(Material.ENDER_PEARL, 2);
                case 6:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(4) + 8);
                case 7:
                    return new ItemStack(Material.ENDER_PEARL, 3);
                case 8:
                    ItemStack item5 = new ItemStack(Material.BOW);
                    item5.addEnchantment(Enchantment.ARROW_DAMAGE, 1);
                    return item5;
                case 9:
                    return new ItemStack(Material.FISHING_ROD);
                case 10:
                    ItemStack item6 = new ItemStack(Material.DIAMOND_SWORD);
                    item6.addEnchantment(Enchantment.DAMAGE_ALL, 1);
                    return item6;
                case 11:
                    return new ItemStack(Material.SNOW_BALL, random.nextInt(5) + 12);
                case 12:
                    return new ItemStack(Material.EGG, random.nextInt(5) + 12);
                case 13:
                    return new ItemStack(Material.GOLDEN_APPLE, 3);
                case 14:
                    return new ItemStack(Material.WEB);
                case 15:
                    return new ItemStack(Material.WATER_BUCKET);
                case 16:
                    return new ItemStack(Material.POTION, 1, (short) 16385);
            }
        } else if (rarity < 80) {
            int i = random.nextInt(22);
            switch (i) {
                case 0:
                    ItemStack item7 = new ItemStack(Material.DIAMOND_BOOTS);
                    item7.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    return item7;
                case 1:
                    ItemStack item8 = new ItemStack(Material.DIAMOND_LEGGINGS);
                    item8.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    return item8;
                case 2:
                    ItemStack item9 = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    item9.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    return item9;
                case 3:
                    ItemStack item10 = new ItemStack(Material.DIAMOND_HELMET);
                    item10.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                    return item10;
                case 4:
                    return new ItemStack(Material.GOLDEN_APPLE, 4);
                case 5:
                    return new ItemStack(Material.EXP_BOTTLE, random.nextInt(2) + 16);
                case 6:
                    return new ItemStack(Material.ENDER_PEARL, 4);
                case 7:
                    return new ItemStack(Material.ENDER_PEARL, 5);
                case 8:
                    return new ItemStack(Material.ARROW, random.nextInt(9) + 32);
                case 9:
                    return new ItemStack(Material.POTION, 1, (short) 16453);
                case 10:
                    return new ItemStack(Material.POTION, 1, (short) 16386);
                case 11:
                    return new ItemStack(Material.POTION, 1, (short) 16385);
                case 12:
                    return new ItemStack(Material.POTION, 1, (short) 16385);
                case 13:
                    return new ItemStack(Material.POTION, 1, (short) 16451);
                case 14:
                    return new ItemStack(Material.POTION, 1, (short) 16388);
                case 15:
                    ItemStack item11 = new ItemStack(Material.DIAMOND_SWORD);
                    item11.addEnchantment(Enchantment.DAMAGE_ALL, 2);
                    return item11;
                case 16:
                    ItemStack item12 = new ItemStack(Material.BOW);
                    item12.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
                    return item12;
                case 17:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 51);
                case 18:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 50);
                case 19:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 95);
                case 20:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 59);
                case 21:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 57);
            }
        } else {
            int i = random.nextInt(11);
            switch (i) {
                case 0:
                    return new ItemStack(Material.POTION, 1, (short) 16421);
                case 1:
                    ItemStack item1 = new ItemStack(Material.BOW);
                    item1.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
                    return item1;
                case 2:
                    return new ItemStack(Material.TNT, 2);
                case 3:
                    ItemStack item2 = new ItemStack(Material.DIAMOND_SWORD);
                    item2.addEnchantment(Enchantment.DAMAGE_ALL, 3);
                    return item2;
                case 4:
                    return new ItemStack(Material.ENDER_PEARL, random.nextInt(4) + 6);
                case 5:
                    return new ItemStack(Material.LAVA_BUCKET);
                case 6:
                    return new ItemStack(Material.MONSTER_EGG, 1, (short) 61);
                case 7:
                    ItemStack item3 = new ItemStack(Material.DIAMOND_BOOTS);
                    item3.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                    return item3;
                case 8:
                    ItemStack item4 = new ItemStack(Material.DIAMOND_HELMET);
                    item4.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                    return item4;
                case 9:
                    ItemStack item5 = new ItemStack(Material.DIAMOND_CHESTPLATE);
                    item5.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                    return item5;
                case 10:
                    ItemStack item6 = new ItemStack(Material.DIAMOND_LEGGINGS);
                    item6.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                    return item6;
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
        return "OP";
    }

    /**
     * Return default kit
     */
    public MPGKit getDefaultKit() {
        return OPDefaultKit.getKit();
    }

}