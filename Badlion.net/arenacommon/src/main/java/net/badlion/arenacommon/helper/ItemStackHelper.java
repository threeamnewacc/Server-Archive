package net.badlion.arenacommon.helper;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemStackHelper {

    public enum ItemType {
        FOOD,
        POTION,
        WEAPON,
        ARMOR,
        SHIELD,
        BLOCK,
        OTHER
    }

    private static Map<Material, ItemType> itemStackTypes = new HashMap<>();

    static {
        // Blocks
        ItemStackHelper.itemStackTypes.put(Material.COBBLESTONE, ItemType.BLOCK);
        ItemStackHelper.itemStackTypes.put(Material.WOOD, ItemType.BLOCK);
        ItemStackHelper.itemStackTypes.put(Material.WATER_BUCKET, ItemType.BLOCK);
        ItemStackHelper.itemStackTypes.put(Material.LAVA_BUCKET, ItemType.BLOCK);

        // Leather armor
        ItemStackHelper.itemStackTypes.put(Material.LEATHER_HELMET, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.LEATHER_CHESTPLATE, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.LEATHER_LEGGINGS, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.LEATHER_BOOTS, ItemType.ARMOR);

        // Chainmail armor
        ItemStackHelper.itemStackTypes.put(Material.CHAINMAIL_HELMET, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.CHAINMAIL_CHESTPLATE, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.CHAINMAIL_LEGGINGS, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.CHAINMAIL_BOOTS, ItemType.ARMOR);

        // Iron armor
        ItemStackHelper.itemStackTypes.put(Material.IRON_HELMET, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.IRON_CHESTPLATE, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.IRON_LEGGINGS, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.IRON_BOOTS, ItemType.ARMOR);

        // Gold armor
        ItemStackHelper.itemStackTypes.put(Material.GOLD_HELMET, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.GOLD_CHESTPLATE, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.GOLD_LEGGINGS, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.GOLD_BOOTS, ItemType.ARMOR);

        // Diamond armor
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_HELMET, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_CHESTPLATE, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_LEGGINGS, ItemType.ARMOR);
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_BOOTS, ItemType.ARMOR);

        // Other armor
        ItemStackHelper.itemStackTypes.put(Material.SKULL_ITEM, ItemType.ARMOR);

        // Weapons
        ItemStackHelper.itemStackTypes.put(Material.BOW, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.ARROW, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.WOOD_AXE, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.WOOD_SWORD, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.STONE_AXE, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.STONE_SWORD, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.IRON_AXE, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.IRON_SWORD, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.GOLD_AXE, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.GOLD_SWORD, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_AXE, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_SWORD, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_PICKAXE, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.FISHING_ROD, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.RAW_CHICKEN, ItemType.WEAPON);
        ItemStackHelper.itemStackTypes.put(Material.DIAMOND_SPADE, ItemType.WEAPON);

        // Special consumables
        ItemStackHelper.itemStackTypes.put(Material.ENDER_PEARL, ItemType.OTHER);
        //ItemStackHelper.itemStackTypes.put(Material.GOD_APPLE, ItemType.OTHER);
        ItemStackHelper.itemStackTypes.put(Material.MILK_BUCKET, ItemType.OTHER);
        ItemStackHelper.itemStackTypes.put(Material.FLINT_AND_STEEL, ItemType.OTHER);
        ItemStackHelper.itemStackTypes.put(Material.SNOW_BALL, ItemType.OTHER);
        ItemStackHelper.itemStackTypes.put(Material.WEB, ItemType.OTHER);

        // Food
        ItemStackHelper.itemStackTypes.put(Material.WHEAT, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.APPLE, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.BAKED_POTATO, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.BREAD, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.COOKED_BEEF, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.COOKED_CHICKEN, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.COOKED_FISH, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.COOKIE, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.GRILLED_PORK, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.MELON, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.PUMPKIN_PIE, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.GOLDEN_CARROT, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.GOLDEN_APPLE, ItemType.FOOD);
        ItemStackHelper.itemStackTypes.put(Material.MUSHROOM_SOUP, ItemType.FOOD);

        // Potions
        ItemStackHelper.itemStackTypes.put(Material.POTION, ItemType.POTION);

		/*ItemStackHelper.itemStackTypes.put(Material.SWIFTNESS_POTION, ItemType.POTION);
        ItemStackHelper.itemStackTypes.put(Material.FIRE_RESISTANCE_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HEALING_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.NIGHT_VISION_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.WEAKNESS_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.LEAPING_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SLOWNESS_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HARMING_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.WATER_BREATHING_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.INVISIBILITY_POTION, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.REGENERATION_POTION_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SWIFTNESS_POTION_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_POTION_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HEALING_POTION_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_POTION_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.LEAPING_POTION_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HARMING_POTION_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.REGENERATION_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SWIFTNESS_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.FIRE_RESISTANCE_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.NIGHT_VISION_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.WEAKNESS_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SLOWNESS_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.LEAPING_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.WATER_BREATHING_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.INVISIBILITY_POTION_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.REGENERATION_POTION_II_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SWIFTNESS_POTION_II_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_POTION_II_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_POTION_II_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.REGENERATION_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SWIFTNESS_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.FIRE_RESISTANCE_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HEALING_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.NIGHT_VISION_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.WEAKNESS_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SLOWNESS_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HARMING_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.BREATHING_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.INVISIBILITY_SPLASH, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.REGENERATION_SPLASH_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SWIFTNESS_SPLASH_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_SPLASH_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HEALING_SPLASH_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_SPLASH_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.LEAPING_SPLASH_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.HARMING_SPLASH_II, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.REGENERATION_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SWIFTNESS_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.FIRE_RESISTANCE_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.NIGHT_VISION_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.WEAKNESS_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.SLOWNESS_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.LEAPING_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.BREATHING_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.INVISIBILITY_SPLASH_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.REGENERATION_SPLASH_II_EXT, ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.POISON_SPLASH_II_EXT , ItemType.POTION);
		ItemStackHelper.itemStackTypes.put(Material.STRENGTH_SPLASH_II_EXT , ItemType.POTION);*/
    }

    public static Map<Material, ItemType> getItemStackTypes() {
        return itemStackTypes;
    }

    public static ItemStack[] clone(ItemStack[] items) {
        ItemStack[] ret = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                ret[i] = items[i].clone();
            }
        }
        return ret;
    }

}
