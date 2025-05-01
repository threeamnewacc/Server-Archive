package net.kohi.vaultbattle.util;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class Soulbound {

    private static final String SOULBOUND_TEXT = ChatColor.YELLOW + "Soulbound";

    public static ItemStack makeSoulbound(ItemStack itemStack) {
        ItemStackUtil.setLore(itemStack, SOULBOUND_TEXT);
        return itemStack;
    }

    public static boolean isSoulbound(ItemStack itemStack) {
        if (itemStack.getItemMeta() != null) {
            if (itemStack.getItemMeta().hasLore()) {
                if (itemStack.getItemMeta().getLore().get(0).equals(SOULBOUND_TEXT)) {
                    return true;
                }
            }
        }
        return false;
    }
}
