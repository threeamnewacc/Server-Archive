package us.zonix.hcfactions.itemdye;

import us.zonix.hcfactions.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.material.MaterialData;
import us.zonix.hcfactions.util.ItemBuilder;

/*
Will be finished later

 */
public enum ItemDye {
    BLACK(ChatColor.BLACK, 0),
    RED(ChatColor.RED, 1),
    DARK_GREEN(ChatColor.DARK_GREEN, 2),
    BLUE(ChatColor.BLUE, 4),
    PURPLE(ChatColor.DARK_PURPLE, 5),
    DARK_AQUA(ChatColor.DARK_AQUA, 6),
    GRAY(ChatColor.GRAY, 7),
    DARK_GRAY(ChatColor.DARK_GRAY, 8),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, 9),
    GREEN(ChatColor.GREEN, 10),
    YELLOW(ChatColor.YELLOW, 11),
    AQUA(ChatColor.AQUA, 12),
    MAGENTA(ChatColor.LIGHT_PURPLE, 9),
    ORANGE(ChatColor.GOLD, 14),
    WHITE(ChatColor.WHITE, 15);

    @Getter private final ChatColor color;
    @Getter private final int data;

    ItemDye(ChatColor color, int data) {
        this.color = color;
        this.data = data;
    }

    public static ItemDye getByData(int data) {
        for (ItemDye dye : values()) {
            if (dye.getData() == data) {
                return dye;
            }
        }
        return null;
    }

    public ItemStack getResult(ItemStack itemStack) {
        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
            return new ItemBuilder(itemStack.clone()).name(color + ChatColor.stripColor(itemStack.getItemMeta().getDisplayName())).build();
        }
        return null;
    }

    public static ShapedRecipe getRecipe(Material material, ItemDye dye) {
        ShapedRecipe toReturn = new ShapedRecipe(new ItemBuilder(Material.DIAMOND_SWORD).name(ChatColor.MAGIC + "ITEM_DYE").build());

        toReturn.shape("QQQ", "QAQ", "QDQ");

        toReturn.setIngredient('Q', Material.QUARTZ);
        toReturn.setIngredient('A', material);
        toReturn.setIngredient('D', new MaterialData(Material.INK_SACK, (byte) dye.getData()));

        return toReturn;
    }

}
