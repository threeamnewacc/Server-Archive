package net.kohi.vaultbattle.util;


import org.bukkit.ChatColor;

public class ChatUtil {

    public static ChatColor getColorFromInt(int amount, int green, int yellow) {
        if (amount >= green) {
            return ChatColor.GREEN;
        } else if (amount >= yellow) {
            return ChatColor.YELLOW;
        } else {
            return ChatColor.RED;
        }
    }
}
